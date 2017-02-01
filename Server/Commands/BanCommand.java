package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.*;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;

/**
 * Created by S199753733 on 12/15/2016.
 *
 * Wrapper class to call the promote command with the argument "rank": "Banned" added, and set the ban reason
 *
 * Arguments: String user, String reason
 *
 * See the PromoteCommand to see what this will return (although this will return returnban instead of returnpromote)
 */
public class BanCommand extends Command {

    public BanCommand() {
        this.name = "ban";
        this.doreturn = true;
        this.minrank = Rank.Op;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        input.put("rank", "Banned");
        User u = gameServer.getUserByName(input.getString("user"));
        if (u != null) {
            u.setBanReason(input.getString("reason"));
        }
        JSONObject jout = new JSONObject(gameServer.commands.getCommand("promote").docommand(clientWorker, gameServer, input, user)) ;
        jout.remove("argument");
        jout.put("argument", "returnban");
        return jout.toString();
    }
}
