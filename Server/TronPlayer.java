package Server;

import javafx.scene.paint.Color;

import java.awt.*;

/**
 * Created by Evan on 12/27/2016.
 *
 * A player class for the tron lobby, that contains the user, it's snake and some propertys.
 */
public class TronPlayer {

    Snake snake;

    User user;

    boolean isAlive = false;

    boolean isVisible = false;

    int score = 0;

    public TronPlayer (User user, Direction direction, Point location, Color color) {
        this.user = user;
        snake = new Snake(0, direction, SnakeType.TRON, location, color);
    }

    public void reset() {
        snake.reset();
        isAlive = true;
        isVisible = true;
    }

}
