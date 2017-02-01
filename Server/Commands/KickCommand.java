package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Jesty.TCPBridge.Clients;
import Server.Authenticate;
import Server.GameServer;
import Server.Rank;
import Server.User;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;

/**
 * Created by Evan on 12/14/2016.
 *
 * Kick the online user from the server. The user will still be able to reauthenticate.
 *
 * Arguments: String user, String reason
 *
 * Returns: Boolean result
 */
public class KickCommand extends Command {

    public KickCommand (){
        this.name = "kick";
        this.doreturn = true;
        this.minrank = Rank.Op;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        StringWriter result = new StringWriter();

        String username = input.getString("user");
        int reason = 0;
        if (!username.equals("")) {
            User u = gameServer.getUserByName(username);
            if (u != null) {
                //Check if the user's rank is greater than the target
                int comparedrank = Authenticate.compareRanks(user.getRank(), u.getRank());
                if (comparedrank > 0) {
                    //System.out.println("bup");
                    gameServer.kick(u, input.getString("reason"));
                    new JSONWriter(result).object()
                            .key("argument").value("returnKick")
                            .key("result").value(true).endObject();
                    return result.toString();
                }
                else {
                    reason = 1;
                }
            }
            else {
                reason = 2;
            }
        }
        else reason = 3;
        new JSONWriter(result).object()
                .key("argument").value("returnKick")
                .key("result").value(false).endObject();
        return result.toString();
    }
}
