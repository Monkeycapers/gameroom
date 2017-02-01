package Client.Commands;

import Client.CanvasFactory;
import Client.GameClient;
import Client.TronCanvas;
import org.json.JSONObject;

/**
 * Created by Evan on 1/31/2017.
 */
public class CanvasCommand extends Command {

    public CanvasCommand() {
        this.name = "canvas";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {
        if (input.getString("type").equals("create")) {
            System.out.println("Creating canvas");
            TronCanvas tronCanvas = new TronCanvas(input.getInt("width"), input.getInt("height"), input.getString("name"));
            gameClient.canvasFactory.addCanvas(tronCanvas);
            tronCanvas.start();
        }
        return null;
    }

}
