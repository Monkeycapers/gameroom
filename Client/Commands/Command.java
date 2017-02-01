package Client.Commands;

import Client.GameClient;
import Jesty.TCPBridge.ClientWorker;
import Server.GameServer;
import Server.Rank;
import Server.User;
import org.json.JSONObject;

/**
 * Created by Evan on 12/13/2016.
 *
 * Base class for a command.
 *
 * A command must set it's name, a boolean that states if it should return anything
 * and it's action in docommand.
 *
 */
public abstract class Command {

    public String name;

    public boolean doReturn;

    public abstract String docommand(GameClient gameClient, JSONObject input);

}
