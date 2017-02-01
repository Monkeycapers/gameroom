package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.GameServer;
import Server.Lobby;
import Server.Rank;
import Server.User;
import org.json.JSONObject;

/**
 * Created by Evan on 1/2/2017.
 *
 * Leaves a lobby and it's chatContext
 *
 * Arguments: NON
 *
 * Does not return
 */
public class LeaveLobbyCommand extends Command {

    public LeaveLobbyCommand () {
        this.name = "leavelobby";
        this.minrank = Rank.User;
        this.doreturn = false;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {

        Lobby lobby = user.getCurrentLobby();
        if (lobby != null) {
            gameServer.lobbys.removeUser(lobby, user);
            gameServer.chatContexts.removeUser(lobby.chatContext, user);
        }

        return "";
    }


}
