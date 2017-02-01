package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Jesty.TCPBridge.Clients;
import Server.Authenticate;
import Server.GameServer;
import Server.Rank;
import Server.User;
import org.json.JSONObject;

/**
 * Created by Evan on 12/14/2016.
 *
 * Stops server execution.
 *
 * Arguments: None
 * Does not return
 * Minimum rank: Op
 */
public class StopCommand extends Command {

    public StopCommand() {
        this.name = "stop";
        this.minrank = Rank.Op;
        this.doreturn = false;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {

        System.out.println("Exiting");
        System.exit(1);

        return "";
    }
}
