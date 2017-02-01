package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONWriter;

/**
 * Created by Evan on 1/16/2017.
 */
public class FileServer {

    HttpServer httpServer;

    int port;

    GameServer gameServer;

    public FileServer(GameServer gameServer, int port) {
        this.port = port;
        this.gameServer = gameServer;
    }

    public void start() {
        try {

            File file = new File("images");
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    System.out.println(f.getName());
                    f.delete();
                }
            }

            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.createContext("/A", new GetFile());
            httpServer.createContext("/U", new UploadFile());
            httpServer.setExecutor(null);
            httpServer.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GetFile implements HttpHandler {

        public GetFile() {

        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = "404 FILE NOT FOUND";
            System.out.println("Query: " + httpExchange.getRequestURI().getQuery());
            System.out.println("The file: " + httpExchange.getRequestURI().getPath());
            String req = httpExchange.getRequestURI().getPath().split("/")[2];
            File file = gameServer.files.getFile(req);
            if (file != null) {
                try {
                    byte[] bytearray = new byte[(int)file.length()];
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    bufferedInputStream.read(bytearray, 0, bytearray.length);

                    httpExchange.sendResponseHeaders(200, file.length());
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(bytearray,0,bytearray.length);
                    os.close();
                    fileInputStream.close();
                }
                catch (Exception e) {
                    sendMessage(httpExchange, response, file.getName());
                }
            }
            else {
                sendError(httpExchange);
            }
        }
    }

    class UploadFile implements HttpHandler {
        @Override
        public void handle(final HttpExchange t) throws IOException {
            for(Map.Entry<String, List<String>> header : t.getRequestHeaders().entrySet()) {
                System.out.println(header.getKey() + ": " + header.getValue().get(0));
            }
            DiskFileItemFactory d = new DiskFileItemFactory();

            try {
                ServletFileUpload up = new ServletFileUpload(d);
                List<FileItem> result = up.parseRequest(new RequestContext() {
                    @Override
                    public String getCharacterEncoding() {
                        return "UTF-8";
                    }

                    @Override
                    public int getContentLength() {
                        return 0; //tested to work with 0 as return
                    }

                    @Override
                    public String getContentType() {
                        return t.getRequestHeaders().getFirst("Content-type");
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return t.getRequestBody();
                    }

                });

                t.getResponseHeaders().add("Content-type", "text/plain");
                t.sendResponseHeaders(200, 0);
                OutputStream os = t.getResponseBody();
                String chat = "null";
                for(FileItem fi : result) {
                    if (fi.getName() != null) {
                        if (fi.getName().startsWith("chatcontext:")) {
                            chat = fi.getName().split(":")[1];
                        }
                        else {
                            System.out.println(fi.getName());
                            System.out.println(os.toString());
                            os.write(fi.getName().getBytes());
                            //os.write("\r\n".getBytes());
                            System.out.println("File-Item: " + fi.getFieldName() + " = " + fi.getName());

                            String name = System.currentTimeMillis() + fi.getName();
                            File file = new File("images/" + name);
                            try {
                                System.out.println("Created the file " + name + " " + file.createNewFile());
                                gameServer.files.addFile(file, name, 600);
                                StringWriter stringWriter = new StringWriter();
                                new JSONWriter(stringWriter).object()
                                        .key("argument").value("image")
                                        .key("name").value(name)
                                        .key("display").value(gameServer.chatContexts.getContext(chat).displayName)
                                        .key("chat").value(chat).endObject();

                                //gameServer.sendToAll(Rank.User, stringWriter.toString());
                                ChatContext chatContext = gameServer.chatContexts.getContext(chat);
                                if (chatContext != null) {
                                    for (User u: chatContext.getUsers()) {
                                        u.clientWorker.sendMessage(stringWriter.toString());
                                    }
                                }

                                FileOutputStream fis = new FileOutputStream(file);
                                fis.write(fi.get());
                                fis.close();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }



//                            File file = new File("images/" + fi.getName());
//                            try {
//                                System.out.println(file.createNewFile());
//                                //Todo: Get the chatcontext name
//                                //Todo: Make a better name for second param
//                                String name = fi.getName() + System.currentTimeMillis();
//                                gameServer.files.addFile(file, name, 60);
//                                StringWriter stringWriter = new StringWriter();
//                                new JSONWriter(stringWriter).object()
//                                        .key("argument").value("image")
//                                        .key("name").value(name)
//                                        .key("display").value(gameServer.chatContexts.getContext(chat).displayName)
//                                        .key("chat").value(chat).endObject();
//                                gameServer.sendToAll(Rank.User, stringWriter.toString());
//                            }
//                            catch (SecurityException e) {
//                                System.out.println("Security");
//                                e.printStackTrace();
//                            }
//                            catch (Exception e) {
//                                System.out.println("Fi: " + fi.getName());
//                                e.printStackTrace();
//                            }
//                            //file.mkdirs();
//                            //file.createNewFile();
//                            FileOutputStream fis = new FileOutputStream(file);
//                            fis.write(fi.get());
//                            fis.close();
                        }
                    }

                }
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            t.close();
        }
    }

    public static void main (String[] args) {
        new FileServer(null, 8000).start();
    }

    public void sendMessage(HttpExchange httpExchange, String message, String filename) {
        try {
            httpExchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=" + filename);

            httpExchange.sendResponseHeaders(200, message.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendError(HttpExchange httpExchange) {
        try {
            //httpExchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=" + filename);
            String message = "ERROR";
            httpExchange.sendResponseHeaders(200, message.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
