package Server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Evan on 1/16/2017.
 */
public class Files {

    List<fileWrapper> fileWrapperList;

    long currenttime = 0;


    public Files() {
        fileWrapperList = new ArrayList<>();
        currenttime = System.currentTimeMillis();
    }

    public void addFile(File file, String name, int cooldown) {
        //update();
        if (file.exists()) {
            file.deleteOnExit();
            fileWrapperList.add(new fileWrapper(name, file, cooldown));
        }
    }

    public void update() {
//        long newTime = System.currentTimeMillis();
//        int timeToRemove = (int)((newTime - currenttime)  / 1000);
//        for (fileWrapper fileWrapper: fileWrapperList) {
//            fileWrapper.exp -= timeToRemove;
//        }
//        fileWrapperList.removeIf(new Predicate<fileWrapper>() {
//            @Override
//            public boolean test(fileWrapper fileWrapper) {
//                boolean destroy = fileWrapper.exp <= 0;
//                if (destroy) {
//                    System.out.println("Deleted File: " + fileWrapper.name + "," + fileWrapper.file.getAbsolutePath() + "," + fileWrapper.file.delete());
//                    return true;
//                }
//                return false;
//            }
//        });
    }
    public File getFile(String name) {
        update();
        for (fileWrapper file: fileWrapperList) {
            if (file.name.equals(name)) return file.file;
        }
        return null;
    }
}
