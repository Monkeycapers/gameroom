package Client.Commands;

import Client.GameClient;
import Client.showSetupGui;
import org.json.JSONObject;

/**
 * Created by Evan on 1/13/2017.
 */
public class returnUserInfoCommand extends Command {

    public returnUserInfoCommand ()  {
        this.name = "userinfo";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {

        showSetupGui.updateUserInfo(input.getString("name"), input.getString("email"), input.getString("rank"));

        return null;
    }

}
