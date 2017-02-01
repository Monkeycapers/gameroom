package Server;

import Jesty.TCPBridge.ClientWorker;

import java.util.List;

/**
 * Created by Evan on 12/19/2016.
 *
 * Abstract class for a chatcontext, that is the bare minimum that a chatContext class must implement
 */
public abstract class ChatContext {

    //The unique name of the chatcontext, should be hidden from users
    public String name;

    //The display name of the chatcontext, is visible to users
    public String displayName;

    //If the chat is general (i.e sends a Connect and Disconnect message)
    boolean isGeneral;

    //Handle a pre-formatted message
    public abstract void sendMessage (String message);

    //Remove a user from the chatcontext, return True if the chat context should be destroyed.
    public abstract boolean removeUser(User user);

    //Get a list of all users in the chat context
    public abstract List<User> getUsers();

    //Do a user action (only called if isGeneral is true)
    public void userAction (String action, User user) {}

}
