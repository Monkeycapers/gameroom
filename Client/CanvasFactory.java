package Client;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 1/31/2017.
 */
public class CanvasFactory {

    private List<CanvasWrapper> canvasList;

    public CanvasFactory() {
        canvasList = new ArrayList<>();
    }

    public void addCanvas(CanvasWrapper canvas) {
        canvasList.add(canvas);
    }

    public void removeCanvas(CanvasWrapper canvas) {
        canvasList.remove(canvas);
    }

    public CanvasWrapper getCanvas(String name) {
        for (CanvasWrapper canvas: canvasList) {
            if (canvas.getName().equals(name)) {
                return canvas;
            }
        }
        return null;
    }



}
