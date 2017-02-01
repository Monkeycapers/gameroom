package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.GameServer;
import Server.OfflineUser;
import Server.Rank;
import Server.User;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;

/**
 * Created by Evan on 12/14/2016.
 *
 * Returns the user info for a user, such as the user's name, email, rank and current lobby
 *
 * Arguments: boolean type
 * IF !type
 * String name
 *
 * Returns: boolean isself, String name, String email, Rank rank, String currentlobby
 */
public class UserInfo extends Command {

    public UserInfo () {
        this.name = "userinfo";
        this.doreturn = true;
        this.minrank = Rank.User;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        StringWriter stringWriter = new StringWriter();
        //System.out.println("Doing user info command");
        if (input.getBoolean("type")) {
            //Type 1: self info
            new JSONWriter(stringWriter).object()
                    .key("argument").value("userinfo")
                    .key("isself").value(true)
                    .key("name").value(user.getName())
                    .key("email").value(user.getEmail())
                    .key("rank").value(user.getRank())
                    .key("currentlobby").value(user.getCurrentLobby().name)
                    .endObject();
        }
        else {
            //Type 2: get Info of another user
            String name = input.getString("name");
            User u = gameServer.getUserByName(name);

            if (u != null) {
                //The user is online, get the info from the user
                new JSONWriter(stringWriter).object()
                        .key("argument").value("userinfo")
                        .key("isself").value(false)
                        .key("name").value(u.getName())
                        .key("email").value(u.getEmail())
                        .key("rank").value(u.getRank())
                        .endObject();
            }
            else {
                //The user is offline, try to get it using the OfflineUser class
                OfflineUser offlineUser = new OfflineUser();
                offlineUser.unSecureSignIn(name);
                //System.out.println("offlineuser");
                new JSONWriter(stringWriter).object()
                        .key("argument").value("userinfo")
                        .key("isself").value(false)
                        .key("name").value(offlineUser.name)
                        .key("email").value(offlineUser.email)
                        .key("rank").value(offlineUser.rank)
                        .key("banreason").value(offlineUser.banreason)
                        .endObject();
            }
        }
        //System.out.println("Returning: " + stringWriter.toString());
        return stringWriter.toString();
    }



}
