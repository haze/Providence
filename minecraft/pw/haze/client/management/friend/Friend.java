package pw.haze.client.management.friend;

/**
 * Created by Haze on 6/4/2015.
 */
public class Friend {

    private String username, alias;

    public Friend(String username, String alias) {
        this.username = username;
        this.alias = alias;
    }

    public String getUsername() {
        return username;
    }

    public String getAlias() {
        return alias;
    }
}
