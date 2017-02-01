package Test;

import Jesty.TCPBridge.Client;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.StringWriter;

/**
 * Created by Evan on 1/4/2017.
 *
 * A test client to test the Server
 */
public class testClient extends Client {

    int id;

    public testClient(int id) {
        super("localhost", 16000);
        this.id = id;
    }

    @Override
    public void onOpen() {
        StringWriter stringWriter = new StringWriter();
        new JSONWriter(stringWriter).object()
                .key("argument").value("signin")
                .key("username").value("test" + id)
                .key("password").value("t")
                .key("email").value("t@t.com")
                .endObject();
        sendMessage(stringWriter.toString());
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);
        if (jsonObject.getString("argument").equals("returnsignin")) {
            StringWriter stringWriter = new StringWriter();
            new JSONWriter(stringWriter).object()
//                    .key("argument").value("chatmessage")
//                    .key("name").value("general")
//                    .key("message").value("/snake")
                    .key("argument").value("joinlobby")
                    .key("name").value("Evan,MyLobby")
                    .endObject();
            sendMessage(stringWriter.toString());
        }
    }

    @Override
    public void onHighPing(long latency) {

    }

}
