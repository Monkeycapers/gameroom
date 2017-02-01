package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.*;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;

/**
 * Created by Evan on 12/16/2016.
 *
 * Checks if the user exists, offline or online.
 *
 * Arguments: String name
 *
 * Returns: boolean result
 */
public class UserExistsCommand extends Command {

    public UserExistsCommand() {
        this.name = "userexists";
        this.doreturn = true;
        this.minrank = Rank.Guest;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        //Step 1: Find a user that is currently connected to the server
        StringWriter result = new StringWriter();
        boolean bresult = false;
        String name = input.getString("name");
        if (name.equals("")) {
            bresult = true;
        }
        else {
            User u = gameServer.getUserByName(name);
            if (u != null) {
                bresult = true;
            }
            else {
                OfflineUser offlineUser = new OfflineUser();
                if (offlineUser.unSecureSignIn(name) == authenticationstatus.Success) {
                    bresult = true;
                }

            }
        }

        new JSONWriter(result).object()
                .key("argument").value("returnuserexists")
                .key("result").value(bresult)
                .endObject();
        return result.toString();
    }
}
