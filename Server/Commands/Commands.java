package Server.Commands;

import Jesty.TCPBridge.ClientWorker;
import Jesty.TCPBridge.Clients;
import Server.Authenticate;
import Server.GameServer;
import Server.User;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by Evan on 12/13/2016.
 *
 * A class that contains all commands, and orchestrates commands, that is given
 * a command name, find a command to execute, and return it's results if necessary
 */
public class Commands {

    ArrayList<Command> commands;

    GameServer gameServer;

    public Commands(GameServer gameServer) {
        //Init the commands ArrayList
        this.gameServer = gameServer;
        commands = new ArrayList<>();
        commands.add(new SignInCommand());
        commands.add(new SignUpCommand());
        commands.add(new StopCommand());
        commands.add(new KickCommand());
        commands.add(new UserInfo());
        commands.add(new BanCommand());
        commands.add(new PromoteCommand());
        commands.add(new UserExistsCommand());
        commands.add(new ChatMessageCommand());
        commands.add(new PmUserCommand());
        commands.add(new JoinLobbyCommand());
        commands.add(new LobbyMessageCommand());
        commands.add(new CreateLobbyCommand());
        commands.add(new LobbyListCommand());
        commands.add(new LeaveLobbyCommand());
        commands.add(new SignOutCommand());
        commands.add(new OfflineUserListCommand());
    }

    /*Perform the command. Returns:
    *"noreturnsuccsess" if the command does not return and was successful
    *"errornocommand" if there was no command found
    *"errornotprivilidged" if the Users rank was lower than the commands minRank
    * OR
    * A JSON String to send to the client
    */
    public String orchestrateCommand(ClientWorker clientWorker, JSONObject jsonObject) {
        StringWriter stringWriter = new StringWriter();
        User user =(User)clientWorker.clientData;
        //Get the command from the argument input
        Command command = getCommand(jsonObject.getString("argument"));
        //If the command was found
        if (command != null) {
            //Check if the User's rank is at least the commands minrank
            if (!Authenticate.checkRank(user.getRank(), command.minrank)) {
                new JSONWriter(stringWriter).object()
                        .key("argument").value("errornotprivilidged").endObject();
                return stringWriter.toString();
            }
            //If the command returns, return the result of the command
            if (command.doreturn) {
                return command.docommand(clientWorker, gameServer, jsonObject, user);
            }
            //Else, return the success String
            else {
                command.docommand(clientWorker, gameServer, jsonObject, user);
                return "noreturnsuccsess";
            }
        }
        //The command was not found
        new JSONWriter(stringWriter).object()
                .key("argument").value("errornocommand").endObject();
        return stringWriter.toString();
    }

    //Find the command from the command List
    public Command getCommand(String argument) {
        for (Command c: commands) {
            if (c.name.equals(argument)) {
                return c;
            }
        }
        return null;
    }


}
