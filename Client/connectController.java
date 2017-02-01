package Client;

import Jesty.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Evan on 1/1/2017.
 *
 * Javafx controller class for the Connect gui, that gets the hostname and portnumber from the user and attempts to
 * connect to the server.
 */
public class connectController implements Initializable {

    @FXML
    TextField hostField;

    @FXML
    TextField portField;

    @FXML
    Button connectButton;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        System.out.println("connect init");
        hostField.setText(Settings.getProperty("host"));
        portField.setText(Settings.getProperty("port"));
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            showSetupGui.showLayout(showSetupGui.rootLayout);
                            String host = hostField.getText();
                            int port = Integer.parseInt(portField.getText());
                            //inputs are good, save them
                            Settings.setProperty("host", host);
                            Settings.setProperty("port", "" + port);
                            Settings.save();
                            showSetupGui.client = new GameClient(host, port);
                            showSetupGui.client.waitStart();
                            JOptionPane.showMessageDialog(null, "Server closed");
                            showSetupGui.showLayout(showSetupGui.connectLayout);
                        }
                    }).start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
