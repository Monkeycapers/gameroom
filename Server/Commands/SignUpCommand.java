package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Jesty.TCPBridge.Clients;
import Server.*;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;

/**
 * Created by Evan on 12/13/2016.
 *
 * Sign up to the server. Takes a name, password and email.
 * If the sign up was succsfull, it will return the same things as signin
 */
public class SignUpCommand extends Command {
    public SignUpCommand() {
        this.name = "signup";
        this.doreturn = true;
        this.minrank = Rank.Guest;
    }

    public String docommand(ClientWorker clientWorker, GameServer gameServer, JSONObject input, User user) {
        StringWriter stringWriter = new StringWriter();
        authenticationstatus status = user.signup(input.getString("username"), input.getString("password"), input.getString("email"));
        if (status == authenticationstatus.Success) {
            new JSONWriter(stringWriter).object()
                    .key("argument").value("returnsignup")
                    .key("success").value(true)
                    .key("users").value(gameServer.getUserList().toArray())
                    .key("displayname").value(user.chatFormatDisplay())
                    .key("highrank").value(Authenticate.checkRank(user.getRank(), Rank.Op))
                    .endObject();
            //Add to general chat
            //((GeneralChat)(gameServer.chatContexts.getContext("general"))).userJoinedMessage(gameServer, user);
            gameServer.chatContexts.userAction("joined", user);
            //Tell the clients to add the user to their user list
            StringWriter writer2 = new StringWriter();
            new JSONWriter(writer2).object()
                    .key("argument").value("updateusers")
                    .key("name").value("general")
                    .key("type").value("add")
                    .key("user").value(user.chatFormatDisplay())
                    .endObject();
            gameServer.sendToPeers(Rank.User, user, writer2.toString());
            //gameServer.chatContexts.doChatMessage(gameServer, user, "general", " has signed in!");
        }
        else {
            int reason = 0;
            new JSONWriter(stringWriter).object()
                    .key("argument").value("returnsignup")
                    .key("success").value(false)
                    .key("reason").value(reason)
                    .endObject();
        }
        return stringWriter.toString();
    }
}
