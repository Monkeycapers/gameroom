package Client.Commands;

import Client.GameClient;
import Jesty.TCPBridge.ClientWorker;

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

    GameClient gameClient;

    public Commands(GameClient gameClient) {
        //Init the commands ArrayList
        this.gameClient = gameClient;
        commands = new ArrayList<>();
        commands.add(new loginGuiCheckUserExistsCommand());
        commands.add(new returnSignInCommand());
        commands.add(new returnSignUpCommand());
        commands.add(new ChatMessageCommand());
        commands.add(new UpdateUsersCommand());
        commands.add(new CloseChatCommand());
        commands.add(new DrawCommand());
        commands.add(new returnLobbyListCommand());
        commands.add(new KickedCommand());
        commands.add(new returnOfflineUserListCommand());
        commands.add(new returnUserInfoCommand());
        commands.add(new ImageCommand());
    }

    public String orchestrateCommand(JSONObject jsonObject) {
        Command command = getCommand(jsonObject.getString("argument"));
        if (command == null) {
            return "errornocommand";
        }
        if (command.doReturn) {
            return command.docommand(gameClient, jsonObject);
        }
        else {
            command.docommand(gameClient, jsonObject);
            return "noreturnsuccsess";
        }
    }

    public Command getCommand(String argument) {
        for (Command c: commands) {
            if (c.name.equals(argument)) {
                return c;
            }
        }
        return null;
    }


}
