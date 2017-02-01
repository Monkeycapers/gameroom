package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.GameServer;
import Server.Rank;
import Server.User;
import org.json.JSONObject;

/**
 * Created by Evan on 1/2/2017.
 *
 * Since a kick is equivalent to signing out, just kick the user from the server
 *
 * Arguments: NONE
 *
 * Does not return
 */
public class SignOutCommand extends Command {

    public SignOutCommand () {
        this.name = "signout";
        this.doreturn = false;
        this.minrank = Rank.User;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        gameServer.kick(user, "Signed out");
        return "";
    }
}
