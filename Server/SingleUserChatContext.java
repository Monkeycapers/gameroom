package Server;

import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 1/3/2017.
 *
 * A chat context that only has one user
 */
public class SingleUserChatContext extends ChatContext {

    User user;

    public SingleUserChatContext (User user, String name, String displayName) {
        this.user = user;
        this.name = name;
        this.displayName = displayName;
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
        user.clientWorker.sendMessage(stringWriter.toString());
    }

    //Disolve the chatcontext if the User is the chat context's user
    @Override
    public boolean removeUser(User user) {
        return this.user == user;
    }

    @Override
    public List<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        return users;
    }
}
