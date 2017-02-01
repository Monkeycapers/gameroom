package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.GameServer;
import Server.Lobby;
import Server.Rank;
import Server.User;
import org.json.JSONObject;

/**
 * Created by Evan on 12/28/2016.
 *
 * Calls the lobby.onMessage of the user's current lobby
 *
 * Argument: String message
 *
 * Does not return
 */
public class LobbyMessageCommand extends Command {

    public LobbyMessageCommand () {
        this.name = "lobbymessage";
        this.minrank = Rank.User;
        this.doreturn = false;
    }


    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {

        Lobby lobby = user.getCurrentLobby();
        if (lobby == null) {
            //Todo: error message
            return "";
        }
        lobby.onMessage(user, input);
        return "";
    }
}
