package pw.haze.client.management.alt;

import java.util.Objects;

/**
 * |> Author: haze
 * |> Since: 3/19/16
 */
public class Account {
    private String username, password;

    public Account(String username) {
        this.username = username;
        this.password = "";
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean isOffline() {
        return Objects.equals(this.password, "");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
