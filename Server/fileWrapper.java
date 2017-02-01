package Server;

import java.io.File;

/**
 * Created by Evan on 1/16/2017.
 */
public class fileWrapper {

    String name;

    File file;

    int exp;

    public fileWrapper(String name, File file, int cooldown) {
        this.exp = cooldown;
        this.file = file;
        this.name = name;
    }

}
