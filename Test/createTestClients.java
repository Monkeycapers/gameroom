package Test;

import java.util.Scanner;

/**
 * Created by Evan on 1/4/2017.
 *
 * Makes batches of test Clients
 */
public class createTestClients {

    public static void main(String[] args) {
        int a = 0;
        while (true) {
            for (int i = a; i < a + 13; i ++) {
                new testClient(i).start();
            }
            a += 13;
            System.out.println("waiting for input");
            new Scanner(System.in).nextLine();
        }
    }

}
