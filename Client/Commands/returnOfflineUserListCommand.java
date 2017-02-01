package Client.Commands;

import Client.GameClient;
import Client.showSetupGui;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by S199753733 on 1/13/2017.
 */
public class returnOfflineUserListCommand extends Command {

    public returnOfflineUserListCommand () {
        this.name = "offlineuserlist";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {

        JSONArray list = input.getJSONArray("list");

        showSetupGui.setOfflineUserList(list);

        return null;
    }

}
