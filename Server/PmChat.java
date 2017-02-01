package Server;

import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 12/22/2016.
 *
 * A private message chatcontext between two users
 */
public class PmChat extends ChatContext {

    User u1, u2;

    public PmChat(User u1, User u2) {
        this.name = "pm " + u1.getName() + ", " + u2.getName();
        this.u1 = u1;
        this.u2 = u2;
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(message, false);
    }
    //If starter, only send the message to the starter of the pm (u1)
    public void sendMessage(String message, boolean starter) {
        //Todo: make this efficient
        StringWriter stringWriter = new StringWriter();
        String displayname = "Error";
        displayname = "Private message with " + u2.getName();
        new JSONWriter(stringWriter).object()
                .key("argument").value("chatmessage")
                .key("name").value(name)
                .key("displayname").value(displayname)
                .key("message").value(message).endObject();

        u1.clientWorker.sendMessage(stringWriter.toString());
        displayname = "Private message with " + u1.getName();
        StringWriter stringWriter2 = new StringWriter();
        new JSONWriter(stringWriter2).object()
                .key("argument").value("chatmessage")
                .key("name").value(name)
                .key("displayname").value(displayname)
                .key("message").value(message).endObject();
        if (!starter) u2.clientWorker.sendMessage(stringWriter2.toString());
    }
    //Figure out what user the target user is (the user who did NOT disconnect), and send closeChat command to the target, then close the lobby
    @Override
    public boolean removeUser(User user) {
        User target = null;
        if (u1.equals(user)) target = u2;
        else if (u2.equals(user)) target = u1;
        if (target != null) {
            System.out.println("Removing user: " + user.chatFormatDisplay());
            StringWriter stringWriter = new StringWriter();
            new JSONWriter(stringWriter).object()
                    .key("argument").value("closechat")
                    .key("name").value(name)
                    .endObject();
            target.clientWorker.sendMessage(stringWriter.toString());
            return true;
        }
        return false;
    }

    @Override
    public List<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);
        return users;
    }
}
