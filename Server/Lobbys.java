package Server;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 12/28/2016.
 *
 * Contains all lobbys, removes users from lobbys and dissolves the lobby if all users have left it
 */
public class Lobbys {

    private List<Lobby> lobbys;

    public Lobbys() {
        lobbys = new ArrayList<>();
    }

    public void addNewLobby(Lobby lobby) {
        lobbys.add(lobby);
    }

    public void removeLobby (Lobby lobby) {
        lobbys.remove(lobby);
    }

    public void doLobbyMessage(User user, String name, JSONObject input) {
        Lobby lobby = getLobbyByName(name);
        if (lobby == null) return;
        lobby.onMessage(user, input);
    }
    //Returns a lobby by the lobby name
    public Lobby getLobbyByName (String name) {
        for (Lobby lobby: lobbys) {
            if (lobby.name.equals(name)) return lobby;
        }
        return null;
    }
    //Remove the user from all lobbys.
    public void removeUser (User user) {
        for (Lobby lobby: lobbys) {
            if (lobby.onClose(user)) {
                //Dissolve the lobby
                removeLobby(lobby);
                lobby.isRunning = false;
                try {
                    lobby.thread.join();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    //Remove the user from the specified lobby
    public void removeUser(Lobby lobby, User user) {
        if (lobby != null || user != null) {
            //If the lobby needs to be dissolved, onClose will return true
            if (lobby.onClose(user)) {
                //Dissolve the lobby
                removeLobby(lobby);
                //Wait for the lobbys thread to end (reach the end of public void run() {});
                //This ensures that the lobby can end safely.
                lobby.isRunning = false;
                try {
                    lobby.thread.join();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //clear the users current lobby
            user.setCurrentLobby(null);
        }
    }
    //Retun a list of the lobbys
    public List<Lobby> getList() {
        return lobbys;
    }


}
