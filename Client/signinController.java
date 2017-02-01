package Client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Evan on 12/15/2016.
 *
 * Javafx controller class for the sign in gui, goal is to sign in or sign up to the server.
 */
public class signinController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private Button loginButton;

    @FXML
    private Button checkButton;

    @FXML
    private Label checkUserExistsLabel;

    @FXML
    private Label signInError;

    @FXML
    private Hyperlink signUpButton;

    @FXML
    private Label confirmPassWordLabel;

    @FXML
    private Label emailLabel;

    boolean isSignUp = false;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        checkButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Check button pressed");
                StringWriter stringWriter = new StringWriter();
                new JSONWriter(stringWriter).object()
                        .key("argument").value("userexists")
                        .key("name").value(usernameField.getText())
                        .endObject();
                showSetupGui.client.sendMessage(stringWriter.toString());
            }
        });

        loginButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Login button pressed");
                if (isSignUp) {
                    StringWriter stringWriter = new StringWriter();
                    new JSONWriter(stringWriter).object()
                            .key("argument").value("signup")
                            .key("username").value(usernameField.getText())
                            .key("password").value(passwordField.getText())
                            .key("email").value(emailField.getText())
                            .endObject();
                    showSetupGui.client.sendMessage(stringWriter.toString());
                    flush();
                }
                else {
                    StringWriter stringWriter = new StringWriter();
                    new JSONWriter(stringWriter).object()
                            .key("argument").value("signin")
                            .key("username").value(usernameField.getText())
                            .key("password").value(passwordField.getText())
                            .endObject();
                    showSetupGui.client.sendMessage(stringWriter.toString());
                    flush();
                }
            }
        });

        signUpButton.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Sign Up button pressed");
                if (isSignUp) {
                    signUpButton.setText("Sign Up");
                    loginButton.setText("Login");
                }
                else {
                    signUpButton.setText("Sign In");
                    loginButton.setText("Sign Up");
                }

                isSignUp = !isSignUp;

                    confirmPasswordField.setText("");
                    confirmPasswordField.setVisible(isSignUp);
                    emailField.setText("");
                    emailField.setVisible(isSignUp);
                    checkButton.setVisible(isSignUp);
                    checkUserExistsLabel.setText("");
                    checkUserExistsLabel.setVisible(isSignUp);

                    confirmPassWordLabel.setVisible(isSignUp);
                    emailLabel.setVisible(isSignUp);
            }
        });

    }

    public void updateCheckIfUserExistsField(String message, boolean success) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                checkUserExistsLabel.setText(message.substring(0, 1) + usernameField.getText() +  message.substring(1));
                if (success) {
                    checkUserExistsLabel.setTextFill(Color.rgb(4, 208, 45));
                }
                else {
                    checkUserExistsLabel.setTextFill(Color.rgb(238, 57, 57));
                }
            }
        });

    }

    public void updateSignInOrSignUpError(String message) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                signInError.setVisible(true);
                signInError.setText(message);
            }
        });
    }

    //Clears all text boxes for user privacy
    public void flush() {
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        signInError.setText("");
        checkUserExistsLabel.setText("");
    }

}
