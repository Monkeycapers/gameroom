package Client;

import Jesty.Settings;


import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Evan on 1/31/2017.
 */
public class TronCanvas extends CanvasWrapper {

    CountDownLatch latch = new CountDownLatch(1);

    List<Object> renderList;

    public TronCanvas(int width, int height, String name) {
        super((width + 1) * Settings.getIntProperty("wallwidth"), (height + 1) * Settings.getIntProperty("wallheight"), name);
    }

    @Override
    public void run() {
        canvas.createBufferStrategy(2);
        BufferStrategy bufferStrategy = canvas.getBufferStrategy();
        while (isRunning) {
            if (renderList != null) {
                //System.out.println("Runnning...");
                //Draw
                Graphics g = bufferStrategy.getDrawGraphics();
                //g.setColor(Color.PINK);
                //g.fillRect(0, 0, width, height);

                int wallWidth = Settings.getIntProperty("wallwidth");
                int wallHeight = Settings.getIntProperty("wallheight");

                g.setColor(Color.BLACK);
                //g.clearRect(0, 0, width * wallWidth, height * wallHeight);
                g.fillRect(0, 0, width * wallWidth, height * wallHeight);

                for (Object object: renderList) {
                    String renderstring = (String)(object);
                    String[] ints = renderstring.split(",");
                    //double d = 100.0;
                    //System.out.println((int)d);
                    Color color = new Color(Integer.valueOf(ints[0]), Integer.valueOf(ints[1]),  Integer.valueOf(ints[2]));
                    g.setColor(color);
                    int x = Integer.valueOf(ints[3]);
                    int y = Integer.valueOf(ints[4]);
                    g.fillRect(wallWidth * x, wallHeight * y, wallWidth, wallHeight);

                    //System.out.println("RenderString: " + renderstring);
                }
                bufferStrategy.show();


                //Wait
                try {
                    //latch.countDown();
                    Thread.sleep(1);
                    //latch = new CountDownLatch(1);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("Render is NULL");
                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void setRenderList(List<Object> renderList) {
        try {
            //latch.await();
            this.renderList = renderList;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
