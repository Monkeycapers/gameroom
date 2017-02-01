package Server;

import Jesty.Settings;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Evan on 1/2/2017.
 *
 * Launches the gameserver class and starts it.
 */
public class launchServer {

    public static void main (String[] args) {
        //Load the Server settings
        HashMap<String, String> defaults = new HashMap<>();
        defaults.put("tronmapwidth", "50");
        defaults.put("tronmapheight", "50");
        defaults.put("tronsleeptime", "100");
        defaults.put("rawport", "16000");
        defaults.put("webport", "8080");
        Settings.setFile(new File("serversettings.txt"), defaults);
        Settings.load();

        //Start the gameServer

        GameServer gameServer = new GameServer(Settings.getIntProperty("rawport"), Settings.getIntProperty("webport"));
        gameServer.start();
    }

}
