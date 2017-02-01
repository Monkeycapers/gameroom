package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.GameServer;
import Server.Lobby;
import Server.Rank;
import Server.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 1/1/2017.
 *
 * Generates a list of the lobbys, their names and displaynames, and the lobbys current player count.
 *
 * Arguments: NON
 *
 * Returns List<JSONObject> lobbylist
 *
 * Each JSONObject: String name, String displayname, String players, boolean isprivate, String gamemode
 *
 */
public class LobbyListCommand extends Command {

    public LobbyListCommand () {
        this.name = "lobbylist";
        this.minrank = Rank.User;
        this.doreturn = true;
    }
    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        StringWriter result = new StringWriter();
        JSONArray list = new JSONArray();
        List<Lobby> lobbys = gameServer.lobbys.getList();
        for (Lobby lobby: lobbys) {
            JSONObject jObject = new JSONObject();
            jObject.put("name", lobby.name);
            jObject.put("displayname", lobby.chatContext.displayName);
            jObject.put("players", lobby.getPlayerCount() + "/" + lobby.maxSize);
            jObject.put("isprivate", lobby.isPrivate);
            jObject.put("gamemode", "Tron");
            list.put(jObject);
        }
        new JSONWriter(result).object()
                .key("argument").value("lobbylist")
                .key("list").value(list)
                .endObject();
        return result.toString();
    }
}
