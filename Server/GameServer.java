package Server;


import Jesty.Settings;
import Jesty.TCPBridge.ClientWorker;
import Jesty.TCPBridge.Server;
import Server.Commands.Commands;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Evan on 10/21/2016.
 *
 * Extends the TCPBridge Server class, handles messages from clients, and passes them to the commands class
 *
 */
public class GameServer extends Server {

    ArrayList<User> users;

    public ChatContexts chatContexts;

    public Commands commands;

    public Lobbys lobbys;

    public Files files;

    public FileServer fileServer;

    public GameServer(int raw_port, int web_port) {
        super(raw_port, web_port);
        setup();
    }

    public GameServer(int port, boolean type) {
        super(port, type);
        setup();
    }

    //Called from constructer
    public void setup() {
        //Load the settings and Authentication
        users = new ArrayList<User>();
        Authenticate.setFile(new File("users.json"));
        Settings.load();
        //Set up commands, chatContexts and lobbys
        commands = new Commands(this);
        chatContexts = new ChatContexts();
        files = new Files();
        files.addFile(new File("test.txt"), "test", 10000000);
        files.addFile(new File("rob.png"), "rob", 1000000);
        //files.addFile(new File("Clustertruck.zip"), "cluster", 1000000);
        fileServer = new FileServer(this, 8000);
        fileServer.start();
        //Default chats
        chatContexts.addNewContext(new RankedChatContext(this, Rank.User, "General chat"));
        chatContexts.addNewContext(new RankedChatContext(this, Rank.Op, "Moderator chat"));
        //
        lobbys = new Lobbys();
    }

    @Override
    public void onMessage(ClientWorker clientWorker, String message) {
        //System.out.println("The message: " + message);
        handleMessage(clientWorker, message);
    }

    //Handle the message. Called by onMessage. Synchronized means that only 1 clientWorker thread can be executing code within the function
    //at a given time.

    public synchronized void handleMessage(ClientWorker clientWorker, String message) {
        try {
            //Get the user from the clientWorker's clientData object
            User user = (User)(clientWorker.clientData);
            //Make a jsonObject from the message
            JSONObject jsonObject = new JSONObject(message);
            //get a result from commands
            String result = commands.orchestrateCommand(clientWorker, jsonObject);
            //if the result is not empty, or the command executed successfully and has things to return,
            //send the result back to the client
            if (!result.equals("noreturnsuccsess") && !result.equals("")) clientWorker.sendMessage(result);
        }
        catch (JSONException e) {
            e.printStackTrace();
            System.out.println("invalid format: " + e.getMessage());
            //Todo: send this back to the client
        }
        catch (Exception e) {
            //Protect the server, and kill the connection
            e.printStackTrace();
            System.out.println("Terminating client connection for client: " + clientWorker);
            clientWorker.forcedisconnect();
        }
    }

    @Override
    public void onClose(ClientWorker clientWorker, int code) {
        handleClose(clientWorker);
    }

    //Handle a lost connection. This method is also be used to kick a user.

    public synchronized void handleClose(ClientWorker clientWorker) {
        User user = (User)(clientWorker.clientData);
        users.remove(user);
        chatContexts.removeUser(user);
        Lobby lobby = user.getCurrentLobby();
        if (lobby != null) {
            lobbys.removeUser(lobby, user);
        }
        if (Authenticate.checkRank(user.getRank(), Rank.User)) {
            chatContexts.userAction("left", user);
        }
        //Tell the clients to remove the user from their user list
        StringWriter writer2 = new StringWriter();
        new JSONWriter(writer2).object()
                .key("argument").value("updateusers")
                .key("name").value("general")
                .key("type").value("remove")
                .key("user").value(user.chatFormatDisplay())
                .endObject();
        //Don't need to use send to peers since the user has already disconnected
        sendToAll(Rank.User, writer2.toString());
    }
    //Handle a gained client. Makes a new User and sets the clientWorker's clientdata to that user.
    @Override
    public void onOpen(ClientWorker clientWorker, int code) {
        handleOpen(clientWorker);
    }
    public synchronized void handleOpen(ClientWorker clientWorker) {
        User user = new User(clientWorker);
        users.add(user);
        clientWorker.clientData = user;
    }
    //
    //Returns a user by the name. Accepts the name outright, or the chat format display
    //IE. Accepts Evan or [User] Evan
    public User getUserByName(String name) {
        for (User u: users) {
            if (u.getName().equals(name) || u.chatFormatDisplay().equals(name)) {
                return u;
            }
        }
        return null;
    }


    //Kick a user from the server. Calls the onClose method, then reconstructs the User class
    //It is as if the client disconnected and reconnected
    public void kick (User user, String reason) {
        ClientWorker targetClientWorker = user.clientWorker;
        onClose(targetClientWorker, 3);
//        users.remove(user);
        //Reconstruct the user
        user = new User(targetClientWorker);
        targetClientWorker.clientData = user;
        users.add(user);
        //Tell the user that they have been kicked
        StringWriter stringWriter = new StringWriter();
        new JSONWriter(stringWriter).object()
                .key("argument").value("kicked")
                .key("reason").value(reason)
                .endObject();
        targetClientWorker.sendMessage(stringWriter.toString());
    }
    //Send a message to all clients of Rank of at least minRank
    public void sendToAll (Rank minRank, String message){
        for (User u: users) {
            if (Authenticate.checkRank(u.getRank(), minRank)) {
                u.clientWorker.sendMessage(message);
            }
        }
    }
    //Send to all except the user of Rank of at least minRank
    public void sendToPeers (Rank minRank, User user, String message) {
        for (User u: users) {
            if ((u != user) && Authenticate.checkRank(u.getRank(), minRank)) {
                u.clientWorker.sendMessage(message);
            }
        }
    }
    //Get a List<String> of all connected users who have authenticated
    public List<String> getUserList () {
        List<String> userlist = new ArrayList<>();
        for (User user: users) {
            if (Authenticate.checkRank(user.getRank(), Rank.User))
            userlist.add("[" + user.getRank() + "] " + user.getName());
        }
        return userlist;
    }
    //get the List of clients from the TCPBridge clients class
    public List<ClientWorker> getClients() {
        return clients.getList();
    }
}
