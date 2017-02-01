package Server;

import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 12/28/2016.
 *
 * Extends ChatContext, stores the users in a list
 */
public class ListChatContext extends ChatContext {

    List<User> users;

    public ListChatContext(User u1, String name, String displayname) {
        users = new ArrayList<>();
        users.add(u1);
        this.name = name;
        this.displayName = displayname;
    }

    @Override
    public void sendMessage(String message) {

        StringWriter stringWriter = new StringWriter();

        new JSONWriter(stringWriter).object()
                .key("argument").value("chatmessage")
                .key("name").value(name)
                .key("displayname").value(displayName)
                .key("message").value(message)
                .endObject();

        for (User u: users) {
            u.clientWorker.sendMessage(stringWriter.toString());
        }

    }

    @Override
    public boolean removeUser (User user) {
        if (users.contains(user)) {
            users.remove(user);
            //If the users list is empty, the chat context should be destroyed
            return users.isEmpty();
        }
        return false;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }
}
