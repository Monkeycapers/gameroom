package Client.Commands;

import Client.GameClient;
import Client.showSetupGui;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Evan on 1/1/2017.
 *
 * Set the lobby list in the lobby list gui to the list given by the server
 */
public class returnLobbyListCommand extends Command {

    public returnLobbyListCommand () {
        this.name = "lobbylist";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {

        JSONArray list = input.getJSONArray("list");

        showSetupGui.setLobbyList(list);
        //list.toList();

        return "";
    }
}
