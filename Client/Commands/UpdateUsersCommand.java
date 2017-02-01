package Client.Commands;

import Client.GameClient;
import Client.showSetupGui;
import org.json.JSONObject;

/**
 * Created by S199753733 on 12/22/2016.
 *
 * Update the userlist in the main client
 */
public class UpdateUsersCommand extends Command {

    public UpdateUsersCommand() {
        this.name = "updateusers";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {

        if (input.getString("type").equals("add")) {
            showSetupGui.addUser(input.getString("user"));
        }
        else {
            showSetupGui.removeUser(input.getString("user"));
        }

        return "";
    }
}
