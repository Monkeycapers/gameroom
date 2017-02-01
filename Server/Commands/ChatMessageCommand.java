package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.ChatContext;
import Server.GameServer;
import Server.Rank;
import Server.User;
import org.json.JSONObject;

/**
 * Created by S199753733 on 12/21/2016.
 *
 * Used to handle chat messages, simply calls chatContexts
 *
 * Arguments: String name, String message
 *
 * Does not return
 */
public class ChatMessageCommand extends Command {

    public ChatMessageCommand() {
        this.name = "chatmessage";
        this.minrank = Rank.User;
        this.doreturn = false;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        gameServer.chatContexts.doChatMessage(gameServer, user, input.getString("name"), input.getString("message"));
        return "";
    }
}
