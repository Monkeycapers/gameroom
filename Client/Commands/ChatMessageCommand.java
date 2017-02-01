package Client.Commands;

import Client.GameClient;
import Client.showSetupGui;
import org.json.JSONObject;

/**
 * Created by Evan on 12/19/2016.
 *
 * Handles chat messages from the server and sends it to showSetupGui
 */
public class ChatMessageCommand extends Command {

    public ChatMessageCommand() {
        this.name = "chatmessage";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {
        String name = input.getString("name");
        String displayName = input.getString("displayname");
        String message = input.getString("message");
        showSetupGui.pushMessage(name, displayName, message);
        return "";
    }
}
