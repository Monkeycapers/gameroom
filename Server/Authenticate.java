package Server;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Evan on 12/6/2016.
 *
 * Static file IO class used to perform actions on the users.json file.
 * Also provides static methods for checking and comparing ranks.
 */
public class Authenticate {

    private static File usersfile;

    private static Rank[] rankOrder = new Rank[] {Rank.Banned, Rank.Guest, Rank.User, Rank.Op, Rank.Admin, Rank.Super};

    //Sets the usersfile to the file, if the file has not been created, create it and add a super user
    public static void setFile(File file) {
        usersfile = file;
        try {
            if (file.createNewFile()) {
                //Create the json object

                JSONArray array = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", "root");
                jsonObject.put("rank", "Super");
                jsonObject.put("email", "");
                jsonObject.put("pass", BCrypt.hashpw("root", BCrypt.gensalt()));
                array.put(jsonObject);

                StringWriter stringWriter = new StringWriter();
                new JSONWriter(stringWriter).object()
                        .key("users").value(array)
                        .endObject();

                PrintWriter out = new PrintWriter(new FileWriter(usersfile));
                out.print(stringWriter.toString());
                out.close();

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Authenticate using a name and a pass
    public static HashMap<String, Object> authenticate(String name, String pass, boolean unsecure) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            String in = "";
            String total = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(usersfile));
            while ((in = bufferedReader.readLine()) != null) {total += in;}
            bufferedReader.close();
            JSONObject jsonObject = new JSONObject(total);
            List<Object> users = jsonObject.getJSONArray("users").toList();
            for (Object u: users) {
                //System.out.println((String)u.toString());
                JSONObject user = new JSONObject((HashMap)u);
                //JSONObject user = new JSONObject((String)u.toString().replaceAll("=", ":"));
                //
                if (user.getString("name").equals(name)) {
                    //Check the hash
                    if (BCrypt.checkpw(pass, user.getString("pass")) || unsecure) {
                        if (user.getString("rank").equals("Banned") && !unsecure) {
                            //banned (bad boy!)
                            result.put("result", false);
                            result.put("reason", 2);
                            result.put("banreason", user.getString("reason"));
                            return result;
                        }
                        //System.out.println("true");
                        result.put("result", true);
                        result.put("name", user.getString("name"));
                        result.put("email", user.getString("email"));
                        result.put("rank", user.getString("rank"));
                        return result;
                    }
                }
            }
            result.put("reason", 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            result.put("reason", 1);
        }
        result.put("result", false);
        return result;
    }
    //Creates a List of all users in the users file
    public static List<String> getUserList() {
        //String toReturn = "";
        try {
            String in = "";
            String total = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(usersfile));
            while ((in = bufferedReader.readLine()) != null) {total += in;}
            bufferedReader.close();
            JSONObject jsonObject = new JSONObject(total);
            List<Object> users = jsonObject.getJSONArray("users").toList();
//            for (Object u: users) {
//                JSONObject user = new JSONObject((HashMap)u);
//                toReturn += "[" +  user.getString("rank") + "] " + user.getString("name") + "\n";
//            }
            List<String> list = new ArrayList<>();
            for (Object u: users) {
                JSONObject user = new JSONObject((HashMap)u);
                //list.add(user.getString("name"));
                list.add("[" +  user.getString("rank") + "] " + user.getString("name"));
            }
            return list;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //Creates a user entry
    public static HashMap<String, Object> signUp(String name, String pass, String email, String rank) {
        HashMap<String, Object> result = new HashMap<>();

        try {
            //Check if the user does not exist
            HashMap<String, Object> checkisalreadyauth = authenticate(name, pass, false);
            if (checkisalreadyauth.containsKey("reason")) {
                //User does not exist
                //Write the user details to file
                String in = "";
                String total = "";
                BufferedReader bufferedReader = new BufferedReader(new FileReader(usersfile));
                while ((in = bufferedReader.readLine()) != null) {total += in;}
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(total);
                JSONArray users = jsonObject.getJSONArray("users");
                JSONObject user = new JSONObject();
                user.put("name", name);
                //Hash the password
                user.put("pass", BCrypt.hashpw(pass, BCrypt.gensalt()));
                user.put("email", email);
                user.put("rank", rank);
                users.put(user);
                StringWriter stringWriter = new StringWriter();
                
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("users", users);
                //System.out.println("JSonObject1 : " + jsonObject1.toString());

               PrintWriter out = new PrintWriter(new FileWriter(usersfile));
               out.print(jsonObject1.toString());
               out.close();

                result.put("result", true);
                result.put("name", name);
                result.put("email", email);
                result.put("rank", rank);
                return result;
            }
            else {
                System.out.println("false because user exists");
                //User already exists, or an error occurred
                //Todo: implemet reason why
                //...//
                result.put("result", false);
                return result;

            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("false because ???");
        result.put("result", false);
        return result;
    }
    //Update a user, if there are any changes ie. Rank change
    public static authenticationstatus update(User user) {
        try {
            String in = "";
            String total = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(usersfile));
            while ((in = bufferedReader.readLine()) != null) {total += in;}
            bufferedReader.close();
            JSONObject jsonObject = new JSONObject(total);
            //System.out.println("jsonobject: " + jsonObject);
            JSONArray users = jsonObject.getJSONArray("users");
            int index = 0;
            int indexOfUser = -1;
            String pass = "";
            for (Object o: users) {
                JSONObject j = (JSONObject)o;
                //System.out.println("J = " + o.getClass().getName());
                if (j.getString("name").equals(user.getName())) {
                    indexOfUser = index;
                    pass = j.getString("pass");
                }
                index++;
            }
            if (indexOfUser != -1) {
                users.remove(indexOfUser);
                JSONObject juser = new JSONObject();
                juser.put("name", user.getName());
                juser.put("pass", pass);
                juser.put("email", user.getEmail());
                juser.put("rank", user.getRank());
                if (user.getRank() == Rank.Banned) {
                    juser.put("reason", user.getBanReason());
                }
                users.put(juser);
            }


            StringWriter stringWriter = new StringWriter();
            new JSONWriter(stringWriter).object()
                    .key("users")
                    .value(users)
                    .endObject();
            //System.out.println("Out: " + stringWriter.toString());
            PrintWriter out = new PrintWriter(new FileWriter(usersfile));
            out.print(stringWriter.toString());
            out.close();
            return authenticationstatus.Success;
            //JSONObject juser = new JSONObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return authenticationstatus.Failure;
    }
    //Update a offline user
    public static authenticationstatus update(OfflineUser user) {
        try {
            String in = "";
            String total = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(usersfile));
            while ((in = bufferedReader.readLine()) != null) {total += in;}
            bufferedReader.close();
            JSONObject jsonObject = new JSONObject(total);
            //System.out.println("jsonobject: " + jsonObject);
            JSONArray users = jsonObject.getJSONArray("users");
            int index = 0;
            int indexOfUser = -1;
            String pass = "";
            for (Object o: users) {
                JSONObject j = (JSONObject)o;
                //System.out.println("J = " + o.getClass().getName());
                if (j.getString("name").equals(user.name)) {
                    indexOfUser = index;
                    pass = j.getString("pass");
                }
                index++;
            }
            if (indexOfUser != -1) {
                users.remove(indexOfUser);
                JSONObject juser = new JSONObject();
                juser.put("name", user.name);
                juser.put("pass", pass);
                juser.put("email", user.email);
                juser.put("rank", user.rank);
                if (user.rank == Rank.Banned) {
                    juser.put("banreason", user.banreason);
                }
                users.put(juser);
            }
            else {
                return authenticationstatus.Failure;
            }


            StringWriter stringWriter = new StringWriter();
            new JSONWriter(stringWriter).object()
                    .key("users")
                    .value(users)
                    .endObject();
            //System.out.println("Out: " + stringWriter.toString());
            PrintWriter out = new PrintWriter(new FileWriter(usersfile));
            out.print(stringWriter.toString());
            out.close();
            return authenticationstatus.Success;
            //JSONObject juser = new JSONObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return authenticationstatus.Failure;
    }



    //TRUE if the rank is atleast minRank
    //FALSE if the rank is < minRank
    public static boolean checkRank (Rank rank, Rank minRank) {
        int i1 = 0, i2 = 0;
        for (int i = 0; i < rankOrder.length; i++) {
            if (rankOrder[i] == (rank)) {
                i1 = i;
            }
            if (rankOrder[i] == (minRank)) {
                i2 = i;
            }
        }
        return (i2 <= i1);
    }

    //1: Rank1 is greater than rank2
    //0: Rank1 is equal to rank2
    //-1 Rank1 is smaller than rank2
    public static int compareRanks (Rank rank1, Rank rank2) {

        //Get the position of the ranks in the rankOrder array

        int i1 = 0, i2 = 0;
        for (int i = 0; i < rankOrder.length; i++) {
            if (rankOrder[i] == (rank1)) {
                i1 = i;
            }
            if (rankOrder[i] == (rank2)) {
                i2 = i;
            }
        }
        //If rank1 > rank2
        if (i1 > i2) return 1;
        //If rank1 < rank2
        if (i1 < i2) return -1;
        //If rank1 == rank2
        return 0;
    }

}
