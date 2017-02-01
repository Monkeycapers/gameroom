package Client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.json.JSONWriter;

import javax.swing.*;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by S199753733 on 1/13/2017.
 */
public class offlineUserActionController implements Initializable {

    @FXML
    ListView userListView;

    @FXML
    Button refreshButton;

    @FXML
    Button banButton;

    @FXML
    Button promoteButton;

    @FXML
    TextArea userInfo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSetupGui.requestOfflineUserList();
            }
        });

        banButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String itemi = String.valueOf(userListView.getSelectionModel().getSelectedItem());
                if (itemi != null) {
                    //code to kick a user
                    //Make a new thread and run it so that the main gui can return while the JOptionPane is open
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String item = itemi;
                            //Remove the [Rank]
                            String[] split = item.split(" ");
                            item = item.substring(split[0].length() + 1);

                            String in = JOptionPane.showInputDialog(null, "Banning User " + item + " \nPlease give a reason: (don't type anything to cancel)");
                            if (in != null) {
                                StringWriter stringWriter = new StringWriter();
                                new JSONWriter(stringWriter).object()
                                        .key("argument").value("ban")
                                        .key("user").value(item)
                                        .key("reason").value(in)
                                        .endObject();
                                showSetupGui.client.sendMessage(stringWriter.toString());
                            }
                        }
                    }).start();
                }
            }
        });

        promoteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String itemi = String.valueOf(userListView.getSelectionModel().getSelectedItem());
                if (itemi != null) {
                    //Remove the [Rank]
//                    String[] split = item.split(" ");
//                    item = item.substring(split[0].length());
                    //code to Promote a user
                    //Make a new thread and run it so that the main gui can return while the JOptionPane is open
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String item = itemi;
                            //Remove the [Rank]
                            String[] split = item.split(" ");
                            item = item.substring(split[0].length() + 1);

                            String in = JOptionPane.showInputDialog(null, "Promoting User " + item + " \nPlease give a Rank to promote to: (don't type anything to cancel)");
                            if (in != null) {

                                StringWriter stringWriter = new StringWriter();
                                new JSONWriter(stringWriter).object()
                                        .key("argument").value("promote")
                                        .key("user").value(item)
                                        .key("rank").value(in)
                                        .endObject();
                                showSetupGui.client.sendMessage(stringWriter.toString());
                            }
                        }
                    }).start();
                }
            }
        });

        userListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String itemi = String.valueOf(userListView.getSelectionModel().getSelectedItem());
                if (itemi != null) {
                    String item = itemi;
                    //Remove the [Rank]
                    String[] split = item.split(" ");
                    item = item.substring(split[0].length() + 1);
                    StringWriter stringWriter = new StringWriter();
                    new JSONWriter(stringWriter).object()
                            .key("argument").value("userinfo")
                            .key("name").value(item)
                            .key("type").value(false)
                            .endObject();
                    showSetupGui.client.sendMessage(stringWriter.toString());
                }
            }
        });


    }


}
