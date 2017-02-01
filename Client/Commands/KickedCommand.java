package Client.Commands;

import Client.GameClient;
import Client.showSetupGui;
import org.json.JSONObject;

import javax.swing.*;

/**
 * Created by Evan on 1/2/2017.
 *
 * Handles being kicked, showing a message dialog and showing the loginlayout
 */
public class KickedCommand extends Command {

    public KickedCommand () {
        this.name = "kicked";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {

        String reason = input.getString("reason");
        showSetupGui.showLayout(showSetupGui.loginLayout);
        showSetupGui.clearAllGuiElements();
        //Separate from the client thread, since JOptionPane waits
        new Thread(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, "Kicked \n" + reason);
            }
        }).start();


        return null;
    }
}
