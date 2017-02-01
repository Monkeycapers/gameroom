package Jesty.TCPBridge;

import org.java_websocket.WebSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;


/**
 * Created by Evan on 10/19/2016.
 *
 * ClientWorker receives and sends messages to specific clients
 */
public class ClientWorker implements Runnable {

    int id;

    boolean isRunning;

    SocketType socketType;

    Socket socket;

    WebSocket webSocket;

    DataInputStream in = null;
    DataOutputStream out = null;

    Server server;

    public Object clientData;

    Thread t;


    public ClientWorker (int id, Socket socket, Server server) {
        this.id = id;
        this.socket = socket;
        socketType = SocketType.RAW_SOCKET;
        this.server = server;
    }

    public ClientWorker(int id, WebSocket socket, Server server) {
        this.id = id;
        this.webSocket = socket;
        socketType = SocketType.WEB_SOCKET;
        this.server = server;
    }

    public void run() {
        //Handle connections with clients
        in = null;
        out = null;
        //Create a DataIn and DataOut Stream
        if (socketType == SocketType.RAW_SOCKET) {
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            }
            catch (Exception e) {
                System.out.println("Could not create in/out streams");
            }
        }
        else {
            //Websockets are already listening in a seperate thread
        }
        isRunning = true;
        while (socketType == SocketType.RAW_SOCKET && isRunning &&  ! getMessage()) { }

        forcedisconnect();
    }

    //Websocket calls this

    //Override this to handle in messages
    public void receiveMessage(Object data) {
        server.onMessage(this, (String)data);
    }

    //Override this if you need to change what is passed to receiveMessage
    public boolean getMessage() {
        boolean failed = true;
        try {
            receiveMessage(in.readUTF());
            failed = false;
        }
        catch (IOException e) {
            //e.printStackTrace();
            //Disconnect
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return failed;
    }
    //

    //Override this if you need to change what is being sent to the client
    public void sendMessage(Object data) {
        if (socketType == SocketType.RAW_SOCKET) {
            try {
                out.writeUTF((String)(data));
//                out.write(((String)(data)).toCharArray().);
            }
            catch (SocketException e) {
                disconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            //System.out.println("Sending " + (String)(data) + " to the websocket" );
            webSocket.send((String)data);
        }

    }
    //Todo: implement codes
    public void disconnect() {
        server.onClose(this, 0);
        server.clients.remove(this);

    }

    public void forcedisconnect() {
        disconnect();
        if (socketType == SocketType.RAW_SOCKET) {
            try {
                in.close();
                out.close();
                socket.close();
                t.join();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            webSocket.close();
        }
    }
    //
    public void connect() {
        server.onOpen(this, 0);
    }
    //


    @Override
    public String toString() {
        InetAddress address;
        if (socketType == SocketType.RAW_SOCKET) {
            address = socket.getInetAddress();
        }
        else {
            address = webSocket.getRemoteSocketAddress().getAddress();
        }
        return "Socket type: " + socketType + ", ip:" +  address;
    }
}
