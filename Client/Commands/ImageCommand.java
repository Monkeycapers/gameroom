package Client.Commands;

import Client.GameClient;
import Client.serverListController;
import Client.showSetupGui;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Evan on 1/17/2017.
 */
public class ImageCommand extends Command {

    public ImageCommand () {
        this.name = "image";
        this.doReturn = false;
    }

    @Override
    public String docommand(GameClient gameClient, JSONObject input) {
        String name = input.getString("name");
        System.out.println(name);
        //Todo: get the name from input

        String chat = input.getString("chat");

        String displayName = input.getString("display");
        //Todo: the port and ip must be given by the server
        if(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif")) {
            showSetupGui.pushImage(chat, displayName, new ImageView("http://localhost:8000/A/" + name));
        }
//        else if (name.endsWith(".html")) {
//            //WebView webView;
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    Label label = new Label();
//                    WebView webView = new WebView();
//                    WebEngine webEngine = webView.getEngine();
//
//                    showSetupGui.pushNode(chat, displayName, webView);
//                    webEngine.load("http://localhost:8000/A/" + name);
//                }
//            });
//
//        }
        else {
            showSetupGui.pushLink(chat, displayName, "http://localhost:8000/A/" + name);
        }
        return "";
    }

//    public void test() {
//        String in = "";
//        try {
////            BufferedReader bufferedReader = new BufferedReader(new FileReader("name.txt"));
////            do {
////                in = bufferedReader.readLine();
////                if (in != null) {
////                }
////            } while (in != null);
//
//            BufferedReader bufferedReader = new BufferedReader(new FileReader("name.txt"));
//
//            Planet mercury = planetCreater(bufferedReader.readLine());
//            Extra extra = extraCreator(bufferedReader.readLine());
//
//            bufferedReader.close();
//
//        }
//
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Planet planetCreater(String line) {
//        String[] split = line.split(",");
//        //Create your planet
//
//        String name = split[0];
//
//
//
//        return new Planet(name, etc);
//    }
//
//    public Extra extraCreator(String line) {
//
//    }

}
