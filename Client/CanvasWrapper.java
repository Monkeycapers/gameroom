package Client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Evan on 1/31/2017.
 */
public abstract class CanvasWrapper implements Runnable {

    protected String name;

    int width, height;

    JFrame jFrame;

    Canvas canvas;

    Thread t;

    boolean isRunning;

    public String getName() {
        return name;
    }

    public CanvasWrapper(int width, int height, String name) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public void start() {
        jFrame = new JFrame();
        jFrame.setLayout(null);
        canvas = new Canvas();
        canvas.setSize(width, height);
        jFrame.add(canvas);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setSize(width, height);
        t = new Thread(this);
        isRunning = true;
        t.start();
    }

    @Override
    public abstract void run();

}
