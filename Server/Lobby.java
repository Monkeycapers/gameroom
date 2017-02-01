package Server;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Evan on 12/6/2016.
 *
 * Abstract class that defines a Lobby.
 */
public abstract class Lobby implements Runnable {
    //Can other people connect (with out a passcode)
    public boolean isPrivate;
    //The name of the lobby
    public String name;
    //The chatcontext of the lobby
    public ChatContext chatContext;
    //Is the lobby currently running? (the public void run() {} should start with while (isRunning) {//Lobby code...})
    public boolean isRunning;
    //The maximum size of the lobby
    public int maxSize;
    //The Lobby thread
    public Thread thread;
    //Start the lobby
    public void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }
    //When a user connects to the lobby
    public abstract void onConnect(User user);
    //When a user leaves the lobby
    //Return true if the lobby is empty
    public abstract boolean onClose(User user);
    //When the user sends a lobbymessage
    public abstract void onMessage(User user, JSONObject input);

    //Pause whatever is going on
    public abstract void pause();

    //Check if the User is eligible to connect
    public abstract boolean canConnect(User user);
    //Get a int of the amount of players in the lobby
    public abstract int getPlayerCount();
    //Return a list of all the users in the lobby
    public abstract List<User> getUsers();

}
