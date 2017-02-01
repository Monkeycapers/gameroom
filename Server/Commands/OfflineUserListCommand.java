package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Server.Authenticate;
import Server.GameServer;
import Server.Rank;
import Server.User;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.List;

/**
 * Created by Evan on 1/13/2017.
 *
 * Returns a list of all offline users
 *
 * Arguments: NONE
 *
 * Returns: List<String> list
 *
 */
public class OfflineUserListCommand extends Command {

    public OfflineUserListCommand() {
        this.name = "offlineuserlist";
        this.minrank = Rank.Op;
        this.doreturn = true;
    }

    @Override
    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        List<String> toSend = Authenticate.getUserList();
        StringWriter stringWriter = new StringWriter();
        new JSONWriter(stringWriter).object()
                .key("argument").value("offlineuserlist")
                .key("list").value(toSend)
                .endObject();
        return stringWriter.toString();
    }

}
