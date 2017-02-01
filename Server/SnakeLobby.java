package Server;

import Jesty.Settings;
import javafx.scene.paint.Color;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 1/3/2017.
 *
 * A Single player Snake game.
 *
 * Access by typing /snake into any chat, or pressing the Snake button under the commands menu on the client
 */
public class SnakeLobby extends Lobby {

    private User user;

    private int mapWidth;

    private int mapHeight;

    private Snake snake;

    private int sleeptime;

    private Body food;

    private Direction changeToDirection;

    private boolean doNotAcceptKeyInput = false;

    public SnakeLobby (User user, ChatContext chatContext) {
        this.chatContext = chatContext;
        this.user = user;
        this.isPrivate = true;
        this.maxSize = 1;
        this.name = user.getName() + "`s snake game";
        isRunning = true;
        snake = new Snake(1, Direction.DOWN, SnakeType.SNAKE);
        //changeToDirection = Direction.DOWN;
        loadProps();
        reset();
    }

    @Override
    public void onConnect(User user) {}

    @Override
    public boolean onClose(User user) {
        return this.user == user;
    }

    @Override
    public void onMessage(User user, JSONObject input) {
        String type = input.getString("type");
        if (type.equals("key") && !doNotAcceptKeyInput){
            //Key input
            String key = input.getString("key");
            //Up
            if (key.equals("W") && !(snake.direction == Direction.DOWN)) {
                changeToDirection = Direction.UP;
                doNotAcceptKeyInput = true;
                //snake.direction = Direction.UP;
            }
            //Down
            else if (key.equals("S") && !(snake.direction == Direction.UP)) {
                changeToDirection = Direction.DOWN;
                doNotAcceptKeyInput = true;
                //snake.direction = Direction.DOWN;
            }
            //Left
            else if (key.equals("A") && !(snake.direction == Direction.RIGHT)) {
                changeToDirection = Direction.LEFT;
                doNotAcceptKeyInput = true;
                //snake.direction = Direction.LEFT;
            }
            //Right
            else if (key.equals("D") && !(snake.direction == Direction.LEFT)) {
                changeToDirection = Direction.RIGHT;
                doNotAcceptKeyInput = true;
                //snake.direction = Direction.RIGHT;
            }
        }
    }

    @Override
    public int getPlayerCount() {
        return 1;
    }

    @Override
    public boolean canConnect(User user) {
        return false;
    }

    @Override
    public List<User> getUsers() {
        List<User> ulist = new ArrayList<>();
        ulist.add(user);
        return ulist;
    }

    //Todo
    @Override
    public void pause() {}

    boolean inVerse = false;

    @Override
    public void run() {
        while (isRunning) {
            long tickTime = System.currentTimeMillis();
//            System.out.println(tick);
//            System.out.println(utick);
            //if (tick == utick) {
                snake.direction = changeToDirection;

                doNotAcceptKeyInput = false;

                boolean doReset = false;

                snake.tick1();

                if (snake.head.xCord > mapWidth || snake.head.xCord < 0 || snake.head.yCord > mapHeight || snake.head.yCord < 0) {
                    doReset = true;
                }
                for (Body body: snake.body) {
                    if (snake.head.xCord == body.xCord && snake.head.yCord == body.yCord) {
                        doReset = true;
                    }
                }
                if (!doReset && snake.head.xCord == food.xCord && snake.head.yCord == food.yCord) {
                    chatContext.sendMessage("Your score: " + snake.body.size());
                    food = randomFood();
                    doReset = (food == null);
                    if (!doReset) {
                        snake.body.push(food);
                    }
                }
                if (doReset) {
                    reset();
                }
                else {
                    snake.tick2();

                    //Render
                    //Gen a list of the snakes points

                    List<String> renderList = new ArrayList<>();
                    for (Body b : snake.body) {
                        renderList.add((int) (b.c.getRed() * 255) + "," + (int) (b.c.getGreen() * 255) + "," + (int) (b.c.getBlue() * 255) + "," + (b.xCord + 1) + "," + (b.yCord + 1));
                    }
                    renderList.add((255) + "," +  (255) + "," + (255) + "," + (snake.head.xCord + 1) + "," + (snake.head.yCord + 1));

                    renderList.add((int) (food.c.getRed() * 255) + "," + (int) (food.c.getGreen() * 255) + "," + (int) (food.c.getBlue() * 255) + "," + (food.xCord + 1) + "," + (food.yCord + 1));

                    StringWriter stringWriter = new StringWriter();
                    new JSONWriter(stringWriter).object()
                            .key("argument").value("lobbydraw")
                            .key("name").value(name)
                            .key("mapwidth").value(mapWidth)
                            .key("mapheight").value(mapHeight)
                            .key("render").value(renderList)
                            .endObject();
                    user.clientWorker.sendMessage(stringWriter.toString());
                    int timeToSleep = (int)(sleeptime - (System.currentTimeMillis() - tickTime));
                    if (timeToSleep > 0) {
                        try {Thread.sleep(timeToSleep);} catch (Exception e) {}
                    }
                    else {
                        try {Thread.sleep(1);} catch (Exception e) {}
                    }
                }
            //}

        }
    }

    public void reset() {
        chatContext.sendMessage("Resetting...\nYour score was " + snake.body.size());
        snake.reset();
        food = randomFood();
        changeToDirection = Direction.DOWN;
    }

    //Search for a food spot randomly, with fallback method for a guaranteed spot or win
    private Body randomFood() {
        Body body = randomFood(mapWidth * mapHeight, 0);
        if (body == null) {
            //Random failed, use a deeper method
            body = deepRandomFood(0, 0);
        }
        if (body != null) {
            body.c = getRandomColor();
        }

        return body;

    }

    //Produce vibrant colors, that can't be grayscale (in theory)
    private Color getRandomColor() {

        int r = (int)(Math.random() * 3);
        int r1 = (55 + (int)(Math.random() * 200));
        int r2 = (55 + (int)(Math.random() * 200));

        if (r == 1) {
            return Color.rgb(r1, r2, 0);
        }
        else if (r == 2) {
            return Color.rgb(0, r1, r2);
        }
        else {
            return Color.rgb(r1, 0, r2);
        }
        //return Color.rgb(55 + (int)(Math.random() * 200), 55 + (int)(Math.random() * 200), 55 + (int)(Math.random() * 200));
    }

    //Deep search for a available food pos
    //Todo: This is currently untested
    private Body deepRandomFood(int xCord, int yCord) {
        Body body = new Body(xCord, yCord, Color.RED);
        //Check if the body is valid
        boolean failed = false;
        if (body.xCord == snake.head.xCord && body.yCord == snake.head.yCord) failed = true;
        for (Body b: snake.body) {
            if (b.xCord == body.xCord && b.yCord == body.yCord) {
                failed = true;
            }
        }
        if (failed) {
            //Check the point to the right
            if (xCord < mapWidth) {
                return deepRandomFood(xCord + 1, yCord);
            }
            //Check one point under, from the left
            yCord++;
            xCord = 0;
            if (yCord < mapHeight) {
                return deepRandomFood(xCord, yCord);
            }
            //Found no spots, user has effectively "beaten" snake
            return null;
        }
        else {
            return body;
        }
    }
    //Find a food spot randomly
    private Body randomFood(int maxIterations, int currentIterations) {
        //if the current iterations is higher than the max, we have failed to find a spot for the food randomly
        if (currentIterations > maxIterations) return null;
        Body body = new Body((int)(Math.random() * mapWidth),(int)(Math.random() * mapHeight), Color.RED);
        //Check if the body is valid
        boolean failed = false;
        if (body.xCord == snake.head.xCord && body.yCord == snake.head.yCord) failed = true;
        if (body.xCord == mapWidth / 2 && body.yCord == mapHeight / 2) failed = true;
        for (Body b: snake.body) {
            if (b.xCord == body.xCord && b.yCord == body.yCord) {
                failed = true;
            }
        }
        //If the spot failed, recurse until a spot is found or the maxIterations is found
        if (failed) {
            return randomFood(maxIterations, currentIterations + 1);
        }
        //Found a spot, return the food Body
        return body;
    }

    //Load from Settings
    public void loadProps() {
        this.mapWidth = Settings.getIntProperty("tronmapwidth");
        this.mapHeight = Settings.getIntProperty("tronmapheight");
        this.sleeptime = Settings.getIntProperty("tronsleeptime");
    }

}
