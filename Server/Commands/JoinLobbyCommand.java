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
 * Joins an existing lobby
 *
 * Arguments: String name
 *
 * Does not return
 */
public class JoinLobbyCommand extends Command {

    public JoinLobbyCommand () {
        this.name = "joinlobby";
        this.doreturn = false;
        this.minrank = Rank.User;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        if (user.getCurrentLobby() == null) {
            String name = input.getString("name");
            Lobby lobby = gameServer.lobbys.getLobbyByName(name);
            if (lobby == null) {
                //Todo
                //Return error message
                return "";
            }
            if (lobby.canConnect(user)) {
                lobby.onConnect(user);
                user.setCurrentLobby(lobby);
            }
        }
        return "";
    }

}
