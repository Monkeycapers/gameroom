package Client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Evan on 12/19/2016.
 *
 * The javafx controller class for the chat tabs, used to handle and display chat messages
 */
public class chatTabController implements Initializable {

//    @FXML
//    TextArea chatTextArea;

    @FXML
    TextFlow textFlow;

    @FXML
    TextField chatTextField;

    @FXML
    Label chatLabel;

    @FXML
    ScrollPane scrollPane;

    String name = "";

    public int pendingMessageCount = 0;

    public String lastDisplayName = "";

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        System.out.println("chatTabController init");

        scrollPane.vvalueProperty().bind(textFlow.heightProperty());

//        VBox container = new VBox();
//        container.getChildren().addAll(textFlow, new HBox(chatLabel, chatTextField));
//        VBox.setVgrow(textFlow, Priority.ALWAYS);
//
//        // Textfield re-sizes according to VBox
//        chatTextField.prefWidthProperty().bind(container.widthProperty());

                chatTextField.setOnAction(new javafx.event.EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                TextField src = ((TextField)(event.getSource()));
                //System.out.println("handling: " + src.getText());
                showSetupGui.handleChatMessage(name, src.getText());
                src.setText("");
                updateUserLabel();
            }
        });

//        textFlow.addListener(new ChangeListener<Object>() {
//            @Override
//            public void changed(ObservableValue<?> observable, Object oldValue,
//                                Object newValue) {
//                //When a new message comes in, scroll to the bottom
//                chatTextArea.setScrollTop(Double.MAX_VALUE);
//                updateUserLabel();
//            }
//        });
        updateUserLabel();
    }

    public int addPendingMessage() {
        pendingMessageCount = pendingMessageCount + 1;
        //System.out.println("Pending messages: " + pendingMessageCount);
        return pendingMessageCount;
    }

    public void updateUserLabel() {
        //Updates the label to the left of the textbox, and shortens long names
        if (showSetupGui.userDisplayName.length() > 29) {
            showSetupGui.userDisplayName = showSetupGui.userDisplayName.substring(0, 29) + "...";
        }
        chatLabel.setText(showSetupGui.userDisplayName);
    }



}
