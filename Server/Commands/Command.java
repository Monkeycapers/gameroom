package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Jesty.TCPBridge.Clients;
import Server.GameServer;
import Server.Rank;
import Server.User;
import org.json.JSONObject;

/**
 * Created by Evan on 12/13/2016.
 *
 * Base class for a command.
 *
 * A command must set it's name, a boolean that states if it should return anything.
 * a min rank, and it's action in docommand.
 *
 */
public abstract class Command {

    public String name;

    boolean doreturn;

    Rank minrank;

    public abstract String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user);

}
