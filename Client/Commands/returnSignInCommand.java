package Client.Commands;

import Client.GameClient;
import Client.showSetupGui;
import Client.signinController;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Evan on 12/16/2016.
 *
 * Handle sign in errors, or setup the main gui and jump to it.
 */
public class returnSignInCommand extends Command {

    public returnSignInCommand () {
        this.name = "returnsignin";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {
            if (input.getBoolean("success")) {
                System.out.println("authed...");

                showSetupGui.isHighRank = input.getBoolean("highrank");
                showSetupGui.userDisplayName = input.getString("displayname");

                JSONArray jsonArray = input.getJSONArray("users");
               // showSetupGui.addUser();
                showSetupGui.showLayout(showSetupGui.outOfMenuLayout);
                showSetupGui.addUsers(jsonArray.toList());
                //showSetupGui.loginloader.getController().
                //((signinController)(showSetupGui.loginloader.getController())).hide();
            }
            else {
                int reason = input.getInt("reason");
                String message = "";
                message = "Error! " + reason;
                if (reason == 2) {
                    message = "Banned for: " + input.getString("banreason");
                }
                ((signinController)(showSetupGui.loginloader.getController())).updateSignInOrSignUpError(message);
            }
            return "";
    }
}
