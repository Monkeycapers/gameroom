package Client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Evan on 1/1/2017.
 *
 * Javafx controller class for the lobby list gui, goal is to allow users to create or join lobbys.
 */
public class lobbyListController implements Initializable {

    @FXML
    ListView listView;

    @FXML
    Button connectButton;

    @FXML
    Button createButton;

    @FXML
    Button refreshButton;

    @FXML
    TextField name;

    @FXML
    TextField maxplayers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("lobby list init");

        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("handling connect button press");
                String val = String.valueOf(listView.getSelectionModel().getSelectedItem());
                System.out.println(val);
                if (val == null) return;
                String realname = showSetupGui.lobbyListHashMap.get(val);
                System.out.println(realname);
                if (realname == null) return;
                StringWriter stringWriter = new StringWriter();
                new JSONWriter(stringWriter).object()
                        .key("argument").value("joinlobby")
                        .key("name").value(realname)
                        .endObject();
                showSetupGui.client.sendMessage(stringWriter.toString());
                showSetupGui.requestLobbyList();
            }
        });

        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                StringWriter stringWriter = new StringWriter();
                new JSONWriter(stringWriter).object()
                        .key("argument").value("createlobby")
                        .key("name").value(name.getText())
                        .key("maxplayers").value(maxplayers.getText())
                        .key("type").value("tron").endObject();
                showSetupGui.client.sendMessage(stringWriter.toString());
                showSetupGui.requestLobbyList();
            }
        });

        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSetupGui.requestLobbyList();
            }
        });


    }

}
