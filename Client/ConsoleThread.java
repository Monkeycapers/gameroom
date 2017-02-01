package Client;

import java.util.Scanner;

/**
 * Created by Evan on 12/16/2016.
 *
 * Test thread for the client that will send the input to the server, used in tandem with the testjson file for realtime testing
 */
public class ConsoleThread implements Runnable {

    @Override
    public void run() {
        try {Thread.sleep(100);}catch (Exception e) { }
        while (true) {
            System.out.print(">");
            showSetupGui.client.sendMessage(new Scanner(System.in).nextLine());
        }
    }
}
