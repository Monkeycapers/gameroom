package Server;


import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/**
 * Created by Evan on 12/10/2016.
 *
 * A Snake class that can be used for Snake and Tron gamemodes
 */
public class Snake {

    Deque<Body> body;

    Body head;

    Direction direction;

    Direction firstdirection = Direction.DOWN;

    Point firstpoint = new Point(0, 0);

    SnakeType type;

    boolean randomcolor;

    Color color = Color.WHITE;

    public Snake() {
        body = new ArrayDeque<>();
        head = new Body(1, 0, Color.WHITE);
        direction = Direction.NONE;
    }

    public Snake(int length, Direction direction, SnakeType type, Color color) {
        body = new ArrayDeque<>();
        head = new Body(0, 0, Color.WHITE);
        grow(length);
        this.direction = direction;
        this.type = type;
        this.color = color;
    }

    public Snake(int length, Direction direction, SnakeType type) {
        this.direction = direction;
        this.type = type;
        randomcolor = true;

        body = new ArrayDeque<>();
        head = new Body(0, 0, Color.WHITE);
        grow(length);
    }


    public Snake(int length, Direction direction, SnakeType type, Point location, Color color) {
        this.direction = direction;
        this.type = type;
        this.color = color;

        firstpoint = location;
        firstdirection = direction;
        body = new ArrayDeque<>();
        head = new Body(location.x, location.y, color);
        grow(length);

    }

    public Snake(int length, Direction direction, SnakeType type, Point location) {
        body = new ArrayDeque<>();
        head = new Body(location.x, location.y, randomColor());
        grow(length);
        this.direction = direction;
        this.type = type;
        randomcolor = true;
    }

    public Color randomColor() {
        return Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }

    public void grow(int increment) {
        for (int i =0; i < increment; i++) {
            if (randomcolor) body.push(new Body(head.xCord, head.yCord, randomColor()));
            else body.push(new Body(head.xCord, head.yCord, head.c));
        }
    }

    public void tick1() {
        //Move the head
        if (direction == Direction.UP) head.yCord -= 1;
        else if (direction == Direction.LEFT) head.xCord -= 1;
        else if (direction == Direction.RIGHT) head.xCord += 1;
        else if (direction == Direction.DOWN) head.yCord += 1;
        //
    }
    public void tick2() {
        //propogate the head and kill the tail
        if (randomcolor)body.addLast(new Body(head.xCord, head.yCord, randomColor()));
        else body.addLast(new Body(head.xCord, head.yCord, head.c));
        if (type == SnakeType.SNAKE) body.pop();
        //
    }

    public void reset() {
        //Resets the snake to its original direction and position.
        direction = firstdirection;
        head.xCord = firstpoint.x;
        head.yCord = firstpoint.y;
        body = new ArrayDeque<>();
        head = new Body(head.xCord, head.yCord, color);
        grow(1);
    }






}
