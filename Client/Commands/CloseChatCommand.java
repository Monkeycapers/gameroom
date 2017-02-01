package Client.Commands;

import Client.GameClient;
import Client.showSetupGui;
import org.json.JSONObject;

/**
 * Created by Evan on 12/27/2016.
 * Handles the closing of a chat tab
 */
public class CloseChatCommand extends Command {

    public CloseChatCommand () {
        this.name = "closechat";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {
        System.out.println("Removing chattab: " + input.getString("name"));
        showSetupGui.removeChatTab(input.getString("name"));
        return "";
    }
}
