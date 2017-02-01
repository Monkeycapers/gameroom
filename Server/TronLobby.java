package Server;

import Jesty.Settings;
import javafx.scene.paint.Color;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.awt.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * Created by Evan on 12/27/2016.
 *
 * The lobby for the tron game. The game will start once two or more people are in the lobby.
 */
public class TronLobby extends Lobby {

    List<TronPlayer> players;

    TronPlayer creator;

    int mapWidth;

    int mapHeight;

    int sleepTime;

    TronState tronState;

    int countdown = 0;

    final int MAX_SUPPORTED_PLAYERS = 12;

    final int DEFAULT_COUNTDOWN_TIME = 3;

    //Ensure all generated colors are not similar
    Color[] colortable = new Color[] {
            Color.RED, Color.GREEN, Color.ORANGE, Color.BLUE, Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTPINK, Color.PURPLE
            , Color.INDIGO, Color.IVORY, Color.MIDNIGHTBLUE, Color.LIMEGREEN, Color.VIOLET
    };

    //The order of directions as people join the server
    Direction[] directiontable = new Direction[] {
        Direction.DOWN, Direction.UP, Direction.RIGHT, Direction.LEFT
    };

    int usersalive = 0;

    public TronLobby(User creator, String lobbyName, int maxPlayers, ChatContext chatContext) {

        //Load all settings
        Settings.load();
        mapWidth = Settings.getIntProperty("tronmapwidth");
        mapHeight = Settings.getIntProperty("tronmapheight");
        sleepTime = Settings.getIntProperty("tronsleeptime");
        //

        //Init variables
        this.name = lobbyName;
        this.chatContext = chatContext;
        players = new ArrayList<>();
        TronPlayer creatorplayer = new TronPlayer(creator, directiontable[0], new Point(0, 0), colortable[0]);
        this.creator = creatorplayer;
        players.add(creatorplayer);
        //Check if the maxPlayers is valid, if not set it automatically to the max supported players
        if (maxPlayers > MAX_SUPPORTED_PLAYERS || maxPlayers <= 1) maxPlayers = MAX_SUPPORTED_PLAYERS;
        this.maxSize = maxPlayers;
        //Wait for one more person to join to start the game
        chatContext.sendMessage("Waiting for 1 more player...");
        tronState = TronState.WAITING;
    }

    @Override
    public boolean onClose(User user) {
        //Remove the user from the lobby
        getChat().removeUser(user);
        TronPlayer player = getPlayerByUser(user);
        if (player == null) return false;
        players.remove(player);
        System.out.println("size: " + players.size());
        if (players.size() <= 1) {
            chatContext.sendMessage("Waiting for 1 more player...");
            tronState = TronState.WAITING;
        }
        //user.setCurrentLobby(null);
        //Transfer ownership
        if (player == creator && !players.isEmpty()) {
            creator = players.get(0);
        }
        //Determine if the lobby needs to be dissolved (if there is no players, it should be dissolved)
        return players.isEmpty();
    }

    @Override
    public void onConnect(User user) {
        user.setCurrentLobby(this);
        getChat().users.add(user);
        TronPlayer player = new TronPlayer(user, directiontable[0], new Point(0, 0), colortable[players.size()]);
        players.add(player);
        //There must have been 1 person waiting for this user to have joined, start the game
        if (tronState == TronState.WAITING) {
            countdown = DEFAULT_COUNTDOWN_TIME;
            tronState = TronState.BEGIN_COUNTDOWN;
        }
    }

    @Override
    public void onMessage(User user, JSONObject input) {
        if (input.getString("type").equals("key")) {
            //Key input
            String key = input.getString("key");
            TronPlayer tronPlayer = getPlayerByUser(user);
            //Make sure the tron player is not null (will throw an expection if it is null)
            assert tronPlayer == null : "Tron player is null; should not be null";
            //if (tronPlayer == null) return;
            //Up
            if (key.equals("W") && !(tronPlayer.snake.direction == Direction.DOWN)) {
                tronPlayer.snake.direction = Direction.UP;
            }
            //Down
            else if (key.equals("S") && !(tronPlayer.snake.direction == Direction.UP)) {
                tronPlayer.snake.direction = Direction.DOWN;
            }
            //Left
            else if (key.equals("A") && !(tronPlayer.snake.direction == Direction.RIGHT)) {
                tronPlayer.snake.direction = Direction.LEFT;
            }
            //Right
            else if (key.equals("D") && !(tronPlayer.snake.direction == Direction.LEFT)) {
                tronPlayer.snake.direction = Direction.RIGHT;
            }
        }
    }

    @Override
    public void pause() {
        //Not implemented
    }

    @Override
    public void run() {
        while (isRunning) {
            //Wait for one more person to join
            if (tronState == TronState.WAITING) {
                try {Thread.sleep(1000);} catch (Exception e) { }
            }
            //In the start countdown, continue it until it is 0 and start the game
            else if (tronState == TronState.BEGIN_COUNTDOWN) {
                chatContext.sendMessage("Time left until game begins: " + countdown);
                countdown --;
                try {Thread.sleep(1000);} catch (Exception e) {}
                if (countdown <= 0) {
                    tronState = TronState.IN_GAME;
                    chatContext.sendMessage("The game has begun!");
                    reset();
                }
            }
            //In the game, perform a tick
            else if (tronState == TronState.IN_GAME) {
                //State changes
                for (TronPlayer tronPlayer: players) {
                    if (tronPlayer.isAlive) {
                        tronPlayer.snake.tick1();
                    }
                }
                //
                //Check for deaths
                for (TronPlayer tronPlayer: players) {
                    if (tronPlayer.isAlive) {
                        //Check if the player has hit the border of the map
                        if (tronPlayer.snake.head.xCord > mapWidth || tronPlayer.snake.head.xCord < 0 || tronPlayer.snake.head.yCord > mapHeight || tronPlayer.snake.head.yCord < 0) {
                            chatContext.sendMessage("Ouch! " + tronPlayer.user.getName() + " ran into a wall!");
                            tronPlayer.isAlive = false;
                        }
                        //Other and self snakes
                        for (TronPlayer otherTronPlayer: players) {
                            if (otherTronPlayer.isVisible) {
                                //If the heads hit and it isn't the same tron
                                if (tronPlayer.snake.head.xCord == otherTronPlayer.snake.head.xCord && tronPlayer.snake.head.yCord == otherTronPlayer.snake.head.yCord && otherTronPlayer.isAlive && tronPlayer != otherTronPlayer) {
                                    //Two heads run into each other
                                    chatContext.sendMessage("Wow! " + tronPlayer.user.chatFormatDisplay() + " and " + otherTronPlayer.user.chatFormatDisplay() + " Killed each other!");
                                    tronPlayer.isAlive = false;
                                    otherTronPlayer.isAlive = false;
                                }
                                else {
                                    for (Body body:otherTronPlayer.snake.body) {
                                        //Check if this snake ran into another snake's body
                                        if (tronPlayer.snake.head.xCord == body.xCord && tronPlayer.snake.head.yCord == body.yCord) {
                                            chatContext.sendMessage(otherTronPlayer.user.getName() + " Killed " + tronPlayer.user.getName() + "!");
                                            tronPlayer.isAlive = false;
                                        }
                                    }
                                }
                            }
                        }
                        //Do the second half of state changes
                        tronPlayer.snake.tick2();
                    }
                }

                //Update the # of users still alive
                usersalive = getUsersStillAlive();

                //Render
                //Gen a list of all snake's points

                List<String> renderList = new ArrayList<>();
                for (TronPlayer tronPlayer: players) {
                    if (tronPlayer.isVisible) {

                        //Add all of the body parts to the list
                        for (Body b : tronPlayer.snake.body) {
                            renderList.add((int) (tronPlayer.snake.color.getRed() * 255) + "," + (int) (tronPlayer.snake.color.getGreen() * 255) + "," + (int) (tronPlayer.snake.color.getBlue() * 255) + "," + (b.xCord + 1) + "," + (b.yCord + 1));
                        }

                        //add the head last so that the body does not overlap with the head
                        renderList.add((255) + "," +  (255) + "," + (255) + "," + (tronPlayer.snake.head.xCord + 1) + "," + (tronPlayer.snake.head.yCord + 1));

                    }
                }
                //Send the render list to all clients
                StringWriter stringWriter = new StringWriter();
                new JSONWriter(stringWriter).object()
                        .key("argument").value("lobbydraw")
                        .key("name").value(name)
                        .key("mapwidth").value(mapWidth)
                        .key("mapheight").value(mapHeight)
                        .key("render").value(renderList)
                        .endObject();
                for (TronPlayer tronPlayer: players) {
                    tronPlayer.user.clientWorker.sendMessage(stringWriter.toString());
                }

                //

                //Check if there is less than 2 people alive, get the winner and start the countdown again
                if (usersalive < 2) {
                    countdown = DEFAULT_COUNTDOWN_TIME;
                    tronState = TronState.BEGIN_COUNTDOWN;
                    TronPlayer winner = getWinner();
                    if (winner == null) {
                        chatContext.sendMessage("Game has finished. Nobody won this game!" + getScoreBoard());
                    }
                    else {
                        chatContext.sendMessage("Game has finished. Winner: " + winner.user.chatFormatDisplay() + getScoreBoard());
                    }
                }

                //Sleep (equivalent of a timer)
                try {Thread.sleep(sleepTime);} catch (Exception e) {e.printStackTrace();}
            }
        }
    }

    //Generates a score board
    public String getScoreBoard() {
        String toSend = "";
        for (TronPlayer tronPlayer: players) {
            toSend += "\n" + tronPlayer.user.chatFormatDisplay() + " Score: " + tronPlayer.score;
        }
        return toSend;
    }

    //This must only be called after users alive has been updated, and users alive is < 2
    //This must also only be called once
    public TronPlayer getWinner() {
        TronPlayer returnPlayer = null;
        for (TronPlayer tronPlayer: players) {
            if (tronPlayer.isAlive) {
                tronPlayer.score++;
                returnPlayer = tronPlayer;
            }
        }

        //Sort the players by their score, using a comparator
        players.sort(new Comparator<TronPlayer>() {
            @Override
            public int compare(TronPlayer o1, TronPlayer o2) {
                if (o1.score > o2.score) {
                    return -1;
                }
                else if (o1.score == o2.score) {
                    return 0;
                }
                else {
                    return 1;
                }
            }
        });
        return returnPlayer;
    }

    //Get the TronPlayer coresponding with the user
    public TronPlayer getPlayerByUser(User u) {
        for (TronPlayer tronPlayer: players) {
            if (tronPlayer.user == u) {
                return tronPlayer;
            }
        }
        return null;
    }

    //Get the amount of users that are still alive
    public int getUsersStillAlive() {
        int ualive = 0;
        for (TronPlayer tronPlayer: players) {
            if (tronPlayer.isAlive) ualive ++;
        }
        return ualive;
    }

    //Reset all of the snakes, determine where they should start, their colors and direction
    public void reset() {

        mapWidth = Settings.getIntProperty("tronmapwidth");
        mapHeight = Settings.getIntProperty("tronmapheight");

        sleepTime = Settings.getIntProperty("tronsleeptime");

        /*
        A Plus is a border

        A number is a player from the player list (sorted by score).

        As you can see, it goes (from direction):
        Down, Up, Right, Left

        And it continues this pattern, filling in the gaps until it reaches 12.

        ++++++++++++++++++++++++++++++++++++
        +       5          1        9      +
        +                                  +
        +7                                8+
        +                                  +
        +3                                4+
        +                                  +
        +                                  +
        +11                              12+
        +                                  +
        +      6           2         10    +
        ++++++++++++++++++++++++++++++++++++

         */



        int multi = 0;
        for (int i = 0; i < players.size(); i ++) {
            TronPlayer player = players.get(i);
            player.reset();
            Point point;

            double divideby = 1;
            boolean doAdd = false;
            if (i <= 3) {
                divideby = 2;
            }
            else if (i <= 7) {
                divideby = 4;
            }
            else if (i <= 11) {
                divideby = 2;
                doAdd = true;
            }

            //Point down
            if (multi == 0) {
                if (doAdd) {
                    point = new Point((int)(mapWidth / divideby) + (mapWidth / 4), 0);
                }
                else {
                    point = new Point((int)(mapWidth / divideby), 0);
                }
            }
            //Point up
            else if (multi == 1) {
                if (doAdd) {
                    point = new Point((int)(mapWidth / divideby) + (mapWidth / 4), mapHeight);
                }
                else {
                    point = new Point((int)(mapWidth / divideby), mapHeight);
                }
            }
            //Point Right
            else if (multi == 2) {
                if (doAdd) {
                    point = new Point(0, (int)(mapHeight / divideby) + (mapHeight / 4));
                }
                else {
                    point = new Point(0, (int)(mapHeight / divideby));
                }
            }
            //Point left
            else if (multi == 3) {
                if (doAdd) {
                    point = new Point(mapWidth, (int)(mapHeight / divideby) + (mapHeight / 4));
                }
                else {
                    point = new Point(mapWidth, (int)(mapHeight / divideby));
                }
            }
            //Point ???
            else {
                point = new Point((int)(mapWidth / divideby), (int)(mapHeight / divideby));
            }

            player.snake = new Snake(1, directiontable[multi], SnakeType.TRON, point, colortable[i]);

            multi ++;
            if (multi >= 4) {
                multi = 0;
            }
            players.set(i, player);
        }
    }


    //Check if the players size is less than the maxSize and check if it is not a private lobby
    @Override
    public boolean canConnect(User user) {
        return (players.size() <= maxSize && !isPrivate);
    }

    //return the players size
    @Override
    public int getPlayerCount() {
        return players.size();
    }

    //Returns the chat context of this lobby
    public ListChatContext getChat() {
        return (ListChatContext)(chatContext);
    }

    //Returns a list of users
    @Override
    public List<User> getUsers() {
        List<User> userlist = new ArrayList<>();
        for (TronPlayer tronPlayer: players) {
            userlist.add(tronPlayer.user);
        }
        return userlist;
    }
}
