package Server;

import Jesty.Settings;
import Jesty.TCPBridge.ClientWorker;
import org.json.JSONWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by S199753733 on 12/21/2016.
 *
 * Contains all of the chatContexts in a list,
 * also includes chat commands
 */
public class ChatContexts {

    private ArrayList<ChatContext> chatContexts;

    public ChatContexts() {
        chatContexts = new ArrayList<>();
    }

    public void addNewContext(ChatContext chatContext) {
        chatContexts.add(chatContext);
    }

    public void removeContext(ChatContext chatContext) {
        chatContexts.remove(chatContext);
    }

    public ChatContext getContext (String name) {
        for (ChatContext chatContext: chatContexts) {
//            System.out.println("The name: " + name);
//            System.out.println("Is the chatContext null?" + (chatContext == null));
//            System.out.println("The chatContext name: " + chatContext.name);
            if (chatContext.name.equals(name)) {
                return chatContext;
            }
        }
    return null;
    }

    public void doChatMessage(GameServer gameServer, User user, String name, String message) {
        //Inner command messages (stuff that does not have a gui)
        //Mostly useful for testing purposes
        if (message.startsWith("/")) {
            String toSend = "";
            if (message.startsWith("/help")) {
                //Todo: do help
                toSend = "List of all inner chat commands:\n" +
                        "/help (Command) --> Gets help for that command\n" +
                        "/list [all, online, RankName, lobby, clients, propertys] --> get a list of users that meet the criteria\n" +
                        "/ping --> Pong!";
            }
            if (message.startsWith("/list all")) {
                List<String> toSendList = Authenticate.getUserList();
                //toSend = Authenticate.getUserList();
                for (String string: toSendList) {
                    toSend += string + "\n";
                }
            }
            else if (message.startsWith("/ping")) {
                toSend = "Pong";
            }
            else if (message.startsWith("/list lobby")) {
                Lobby lobby = user.getCurrentLobby();
                if (lobby != null) {
                    List<User> users = lobby.getUsers();
                    for (User u: users) {
                        toSend += u.chatFormatDisplay() + "\n";
                    }
                }
            }
            else if (message.startsWith("/list clients")) {
                if (Authenticate.checkRank(user.getRank(), Rank.Op)) {
                    for (ClientWorker w: gameServer.getClients()) {
                        toSend += w.toString() + "\n";
                    }
                }
            }
            else if (message.startsWith("/list propertys")) {
                if (Authenticate.checkRank(user.getRank(), Rank.Op)) {
                    toSend = Settings.listPropertys();
                }
            }
            else if (message.startsWith("/changeproperty")) {
                if (Authenticate.checkRank(user.getRank(), Rank.Op)) {
                    String[] split = message.split(" ");
                    Settings.setProperty(split[1], split[2]);
                    toSend = "Changed property: " + split[1] + " to " + split[2] + ".\n" + Settings.listPropertys();
                }
            }
            else if (message.startsWith("/loadpropertys")) {
                if (Authenticate.checkRank(user.getRank(), Rank.Op)) {
                    Settings.load();
                    toSend = "Loaded propertys.\n" + Settings.listPropertys();
                }
            }

            else if (message.startsWith("/snake")) {
                if (user.getCurrentLobby() == null) {
                    SingleUserChatContext chatContext = new SingleUserChatContext(user, "Snake-->" + user.getName(), "Snake");
                    addNewContext(chatContext);
                    SnakeLobby snakeLobby = new SnakeLobby(user, chatContext);
                    user.setCurrentLobby(snakeLobby);
                    gameServer.lobbys.addNewLobby(snakeLobby);
                    snakeLobby.start();
                }
            }

            else if (message.startsWith("/statesnake")) {
                if (user.getCurrentLobby() == null) {
                    SingleUserChatContext chatContext = new SingleUserChatContext(user, "Snake-->" + user.getName(), "Snake");
                    addNewContext(chatContext);
                    StateSnakeLobby snakeLobby = new StateSnakeLobby(user, chatContext);
                    user.setCurrentLobby(snakeLobby);
                    gameServer.lobbys.addNewLobby(snakeLobby);
                    snakeLobby.start();
                }
            }

            else if (message.startsWith("/leavelobby")) {
                Lobby lobby = user.getCurrentLobby();
                if (lobby != null) {
                    gameServer.lobbys.removeUser(lobby, user);
                    toSend = "Left the lobby";
                }
            }

//            else if (message.startsWith("/load")) {
//                String[] split = message.split(" ");
//                File file = new File(split[1]);
//                System.out.println(file.exists());
//                if (file.exists()) {
//                    try {
//                        String in = "";
//                        BufferedReader reader = new BufferedReader(new FileReader(file));
//                        while ((in = reader.readLine()) != null) {toSend += in + "\n";}
//                        reader.close();
//                        //toSend = in;
//                        //Get the chat context, if it exists send the chat message
//                        ChatContext chatContext = getContext(name);
//                        if (chatContext == null) return;
//                        chatContext.sendMessage(toSend);
//                        toSend = "";
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

            if (!toSend.isEmpty()) {
                StringWriter stringWriter = new StringWriter();
                new JSONWriter(stringWriter).object()
                        .key("argument").value("chatmessage")
                        .key("name").value("Server-->" + user.getName())
                        .key("displayname").value("Server")
                        .key("message").value(toSend)
                        .endObject();
                user.clientWorker.sendMessage(stringWriter.toString());
            }

        }
        else {
            //Get the chat context, if it exists send the chat message
            ChatContext chatContext = getContext(name);
            if (chatContext == null) return;
            chatContext.sendMessage(user.chatFormatDisplay() + " " + message);
        }
    }

    //Remove a user from all chat contexts, if the chat context returns true remove it
    public void removeUser(User user) {
        for (ChatContext chatContext: chatContexts) {
            if (chatContext.removeUser(user)) {
                //Dissolve the chat
                removeContext(chatContext);
                break;
            }
        }
    }

    //Perform a userAction (action can be Joined or Left), only calls it if the chatContext is general
    public void userAction (String action, User user) {
        for (ChatContext chatContext: chatContexts) {
            if (chatContext.isGeneral) chatContext.userAction(action, user);
        }
    }

    //Remove a user from a specific chatContext.
    public void removeUser (ChatContext chatContext, User user) {
        if (chatContext.removeUser(user)) {
            removeContext(chatContext);
        }
    }
}
