package Client;

import Jesty.Settings;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.json.JSONWriter;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.io.StringWriter;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by S199753733 on 12/19/2016.
 *
 *Javafx controller class for the main gui, contains the chat tab(s), the player list, the menu and the game Canvas.
 */

//todo: Refactor this, terrible name
public class serverListController implements Initializable {

    @FXML
    TabPane chatTabPane;

    @FXML
    MenuItem lobbyListMenuItem;

    @FXML
    MenuItem signOutMenuItem;

    @FXML
    MenuItem pmMenuItem;

    @FXML
    MenuItem kickMenuItem;

    @FXML
    MenuItem banMenuItem;

    @FXML
    MenuItem promoteMenuItem;

    @FXML
    MenuItem leaveLobbyMenuItem;

    @FXML
    MenuItem snakeLobbyItem;

    @FXML
    MenuItem offlineUserAction;

    //User List view

    @FXML
    ListView userListView;

    HashMap<Tab, chatTabController> tabControllerHashMap;

    HashMap<String, Tab> tabNameHashMap;


    //Draw to canvas
    public void draw() {
//        showSetupGui.updateCanvas();
//
//        int height = Integer.valueOf(Settings.getProperty("wallheight"));
//        int width = Integer.valueOf(Settings.getProperty("wallwidth"));
//
//        GraphicsContext gc = showSetupGui.canvas.getGraphicsContext2D();
//
//        gc.setFill(Color.BLACK);
//
//        gc.clearRect(0, 0, showSetupGui.mapWidth * width, showSetupGui.mapHeight * height);
//        gc.fillRect(0, 0, showSetupGui.mapWidth * width, showSetupGui.mapHeight * height);
//
//
//        for (Object object: showSetupGui.render) {
//            String renderstring = (String)(object);
//            String[] ints = renderstring.split(",");
//            //double d = 100.0;
//            //System.out.println((int)d);
//            Color color = Color.rgb(Integer.valueOf(ints[0]), Integer.valueOf(ints[1]),  Integer.valueOf(ints[2]));
//            gc.setFill(color);
//            int x = Integer.valueOf(ints[3]);
//            int y = Integer.valueOf(ints[4]);
//            gc.fillRect(width * x, height * y, width, height);
//
//            //System.out.println("RenderString: " + renderstring);
//        }
//        gc.setFill(Color.WHITE);
//        //Top wall
//        gc.fillRect(0, 0, (showSetupGui.mapWidth * width), height);
//        //Left wall
//        gc.fillRect(0, 0, width, (showSetupGui.mapHeight * height));
//        //Right wall
//        gc.fillRect((showSetupGui.mapWidth * width), 0, width, (showSetupGui.mapHeight * height));
//        //Bottom wall
//        gc.fillRect(0, (showSetupGui.mapHeight * height), (showSetupGui.mapWidth * width), height);
//
//        gc.save();
    }




    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        System.out.println("Init Server List Gui");
        //Force the canvas to register all key inputs
//        canvas.setFocusTraversable(true);
//        canvas.addEventFilter(MouseEvent.ANY, (e) -> canvas.requestFocus());
//        new AnimationTimer() {
//            public void handle(long currentNanoTime) {
//                draw();
//            }
//        }.start();

        tabControllerHashMap = new HashMap<>();
//        chatTabPane.getTabs().add(getNewTab("Wow!", "Wow"));
//        chatTabPane.getTabs().add(getNewTab("much", "lol"));
//        chatTabPane.getTabs().add(getNewTab("panes", "bup"));

        //userListView.getItems().addAll("User1", "User2", "User3");


        offlineUserAction.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSetupGui.requestOfflineUserList();
                showSetupGui.showAnotherLayout(showSetupGui.offlineUserActionLayout);
            }
        });

        lobbyListMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSetupGui.requestLobbyList();
                showSetupGui.showAnotherLayout(showSetupGui.lobbyListLayout);
            }
        });

        signOutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                StringWriter stringWriter = new StringWriter();
        new JSONWriter(stringWriter).object()
                .key("argument").value("signout")
                .endObject();
        showSetupGui.client.sendMessage(stringWriter.toString());
            }
        });

        pmMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String item = String.valueOf(userListView.getSelectionModel().getSelectedItem());
                if (item != null) {
                    //code to pm a user
                    StringWriter stringWriter = new StringWriter();
                    new JSONWriter(stringWriter).object()
                            .key("argument").value("pmuser")
                            .key("user").value(item)
                            .endObject();
                    showSetupGui.client.sendMessage(stringWriter.toString());
                    //
                }
            }
        });

        kickMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String item = String.valueOf(userListView.getSelectionModel().getSelectedItem());
                if (item != null) {
                    //code to kick a user
                    //Make a new thread and run it so that the main gui can return while the JOptionPane is open
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String in = JOptionPane.showInputDialog(null, "Kicking User " + item + " \nPlease give a reason: (don't type anything to cancel)");
                            if (in != null) {
                                StringWriter stringWriter = new StringWriter();
                                new JSONWriter(stringWriter).object()
                                        .key("argument").value("kick")
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

        banMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String item = String.valueOf(userListView.getSelectionModel().getSelectedItem());
                if (item != null) {
                    //code to kick a user
                    //Make a new thread and run it so that the main gui can return while the JOptionPane is open
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
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

        promoteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String item = String.valueOf(userListView.getSelectionModel().getSelectedItem());
                if (item != null) {
                    //code to Promote a user
                    //Make a new thread and run it so that the main gui can return while the JOptionPane is open
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
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

        leaveLobbyMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                StringWriter stringWriter = new StringWriter();
                new JSONWriter(stringWriter).object()
                        .key("argument").value("leavelobby")
                        .endObject();
                showSetupGui.client.sendMessage(stringWriter.toString());
            }
        });

        snakeLobbyItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                StringWriter stringWriter = new StringWriter();
                new JSONWriter(stringWriter).object()
                        .key("argument").value("createlobby")
                        .key("type").value("snake")
                        .endObject();
                showSetupGui.client.sendMessage(stringWriter.toString());
            }
        });





//        userListView.setCellFactory(lv -> {
//
//            ListCell<String> cell = new ListCell<>();
//
//            ContextMenu contextMenu = new ContextMenu();
//
//            MenuItem pmItem = new MenuItem();
//            pmItem.textProperty().bind(Bindings.format("Pm \"%s\"", cell.itemProperty()));
//            pmItem.setOnAction(event -> {
//                String item = cell.getItem();
//                // code to pm a user
////                StringWriter stringWriter = new StringWriter();
////                new JSONWriter(stringWriter).object()
////                        .key("argument").value("pmuser")
////                        .key("name").value(item)
////                        .endObject();
////                showSetupGui.client.sendMessage(stringWriter.toString());
//            });
//            contextMenu.getItems().addAll(pmItem);
//
//                MenuItem banItem = new MenuItem();
//                banItem.textProperty().bind(Bindings.format("Ban \"%s\"", cell.itemProperty()));
//                banItem.setOnAction(event -> {
//                    String item = cell.getItem();
//                    //Send a message to ban a user
//                });
//                //contextMenu.getItems().addAll(banItem);
//
//                MenuItem kickItem = new MenuItem();
//                kickItem.textProperty().bind(Bindings.format("Kick \"%s\"", cell.itemProperty()));
//                kickItem.setOnAction(event -> {
//                    String item = cell.getItem();
//                    //Send a message to kick a user
//                });
//
//                MenuItem promoteItem = new MenuItem();
//                promoteItem.textProperty().bind(Bindings.format("Promote \"%s\"", cell.itemProperty()));
//                promoteItem.setOnAction(event -> {
//                    String item = cell.getItem();
//                    //Send a message to promote a user
//                });
//
//                contextMenu.getItems().addAll(kickItem, banItem, promoteItem);
//
//
//            cell.textProperty().bind(cell.itemProperty());
//
//            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
//                if (isNowEmpty) {
//                    cell.setContextMenu(null);
//                } else {
//                    cell.setContextMenu(contextMenu);
//                }
//            });
//            return cell ;
//        });

        chatTabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        try {
                            //System.out.println("Tab Selection changed");
                            chatTabController controller = tabControllerHashMap.get(t1);
                            if (controller != null) {
                                controller.pendingMessageCount = 0;
                                if (t1 != null  && controller.lastDisplayName != null)
                                    t1.setText(controller.lastDisplayName);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        tabNameHashMap = new HashMap<>();

        userListView.getItems().add("Offline User");

//        generalTextField.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println(event.getSource());
//                System.out.println("Text field event");
//            }
//        });


    }

    public Tab getNewTab(String name, String displayname) {
        Tab tab = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(showSetupGui.class.getResource("chatComponent.fxml"));
        try {
            tab = fxmlLoader.load();
            tab.setText(displayname);
            chatTabController controller = fxmlLoader.getController();
            controller.name = name;
            controller.lastDisplayName = displayname;
            tabControllerHashMap.put(tab, fxmlLoader.getController());
            tabNameHashMap.put(name, tab);
            showSetupGui.addChatTab(name, fxmlLoader.getController());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tab;
    }

    public void addNewTab(String name, String displayname) {
        Tab tab = getNewTab(name, displayname);
        chatTabPane.getTabs().add(tab);
    }

    public void removeTab (Tab tab) {
        chatTabPane.getTabs().remove(tab);
    }

    public void addUser(String text) {
        userListView.getItems().add(text);
    }

    public void removeUser (String text) {
        userListView.getItems().remove(text);
    }


    public void sort() {
        userListView.getItems().sort(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                //This works as indented because the possible ranks are (from most to least power):
                //Admin, Op, User, Guest
                //The first letter of each of them is lower down the alphabet then the previous
                //tl;dr: I kind of got lucky here
                //Also, if they are the same rank, then it will compare the names, so ranks can be grouped together
                return String.valueOf(o1).compareTo(String.valueOf(o2));
            }
        });
    }
}