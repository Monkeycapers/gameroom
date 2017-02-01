package Server;

import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 1/11/2017.
 */
public class RankedChatContext extends ChatContext {

    Rank minRank;

    GameServer gameServer;

    //A General chat context that will send messages to all users above or at a current rank. Used for the
    //general chat and moderator chat

    public RankedChatContext(GameServer gameServer, Rank rank, String name) {
        this.minRank = rank;
        this.name = rank.name() + "," + name;
        this.displayName = name;
        this.gameServer = gameServer;
        isGeneral = true;
    }

    @Override
    public boolean removeUser(User user) {
        return false;
    }

    @Override
    public void sendMessage(String message) {
        //Send the message to all users above the minRank
        StringWriter stringWriter = new StringWriter();
        new JSONWriter(stringWriter).object()
                .key("argument").value("chatmessage")
                .key("name").value(name)
                .key("message").value(message)
                .key("displayname").value(displayName)
                .endObject();
        gameServer.sendToAll(minRank, stringWriter.toString());
    }

    @Override
    public void userAction(String action, User user) {
        //Tells people that the user has joined or left the server. This must be send when joined so that the joiner can participate in the chat
        sendMessage("* [" + user.getRank() + "] " +  user.getName() + " has " + action + " the server! Server population: " + (gameServer.getUserList().size()) + " *");
    }

    @Override
    public List<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        for (User u: gameServer.users) {
            if (Authenticate.checkRank(u.getRank(), minRank)) {
                users.add(u);
            }
        }
        return users;
    }
}
