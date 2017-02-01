package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.*;
import org.json.JSONObject;

/**
 * Created by Evan on 12/28/2016.
 *
 * Creates a tron lobby, and sets the user's current lobby to this lobby.
 * Also creates a list chatcontext for the lobby
 *
 * Arguments: String type, String name, int maxplayers
 *
 * Does not return
 */
public class CreateLobbyCommand extends Command {

    public CreateLobbyCommand () {
        this.name = "createlobby";
        this.minrank = Rank.User;
        this.doreturn = false;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        if (user.getCurrentLobby() == null) {
            String type = input.getString("type");
            if (type.equals("tron")) {
                String name = user.getName() + "," + input.getString("name");
                ListChatContext chatContext = new ListChatContext(user, name, input.getString("name"));
                gameServer.chatContexts.addNewContext(chatContext);
                TronLobby tronLobby = new TronLobby(user, name, input.getInt("maxplayers"), chatContext);
                gameServer.lobbys.addNewLobby(tronLobby);
                user.setCurrentLobby(tronLobby);
                tronLobby.start();
            }
            else if (type.equals("snake")) {
                SingleUserChatContext chatContext = new SingleUserChatContext(user, "Snake-->" + user.getName(), "Snake");
                gameServer.chatContexts.addNewContext(chatContext);
                SnakeLobby snakeLobby = new SnakeLobby(user, chatContext);
                user.setCurrentLobby(snakeLobby);
                gameServer.lobbys.addNewLobby(snakeLobby);
                snakeLobby.start();
            }
        }
        return "";
    }
}
