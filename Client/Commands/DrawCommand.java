package Client.Commands;

import Client.GameClient;
import Client.TronCanvas;
import Client.showSetupGui;
import Jesty.Settings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.List;

/**
 * Created by Evan on 12/28/2016.
 *
 * Handle render command from server, push to showSetupGui
 */
public class DrawCommand extends Command {

    public DrawCommand () {
        this.name = "lobbydraw";
        //this.doReturn = true;
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {
        List<Object> render = input.getJSONArray("render").toList();
        int mapWidth = input.getInt("mapwidth") + 2;
        int mapHeight = input.getInt("mapheight") + 2;
        //showSetupGui.render(render, mapWidth, mapHeight);

        ((TronCanvas)(gameClient.canvasFactory.getCanvas(input.getString("name")))).setRenderList(render);


//        StringWriter stringWriter = new StringWriter();
//
//        new JSONWriter(stringWriter).object()
//                .key("argument").value("lobbymessage")
//                .key("type").value("wasdrawn")
//                .endObject();
//
//        return stringWriter.toString();
        return null;
    }

}
