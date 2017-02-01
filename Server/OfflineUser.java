package Server;

import Jesty.TCPBridge.ClientWorker;

import java.util.HashMap;

/**
 * Created by Evan on 12/15/2016.
 *
 * An unsecure, offline user that imitates a User. Used for promoting and banning offline users
 */
public class OfflineUser {

    public String name;

    public String email;

    public Rank rank;

    public String banreason;

    public authenticationstatus unSecureSignIn(String name) {
        HashMap<String, Object> result = Authenticate.authenticate(name, "", true);
        if ((boolean)result.get("result")) {
            this.name = (String)result.get("name");
            this.email = (String)result.get("email");
            this.rank = Rank.valueOf((String)result.get("rank"));
            return authenticationstatus.Success;
        }
        return authenticationstatus.Failure;
    }

    public authenticationstatus unSecureUpdateRank (Rank rank) {
        this.rank = rank;
        return Authenticate.update(this);
    }

}
