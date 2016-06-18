package pw.haze.client.management.alt;

import pw.haze.client.interfaces.Loadable;
import pw.haze.client.interfaces.Savable;
import pw.haze.client.management.ListManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * |> Author: haze
 * |> Since: 3/19/16
 */
public class AccountManager extends ListManager<Account> implements Savable, Loadable {

    public AccountManager() {
        super("Accounts");
    }

    @Override
    public void load() throws Exception {
        Properties properties = new Properties();
        if (getFile("data").exists()) {
            try (InputStream stream = new FileInputStream(getFile("data"))) {
                properties.load(stream);
                for (Map.Entry entry : properties.entrySet()) {
                    String username = entry.getKey().toString();
                    String password = entry.getValue().toString().toLowerCase().equals("n/a") ? "" : entry.toString();
                    this.contents.add(new Account(username.trim(), password.trim()));
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    @Override
    public void save() throws Exception {
        Properties properties = new Properties();
        try (OutputStream stream = new FileOutputStream(getFile("data"))) {
            this.contents.stream().forEach((acc) -> properties.setProperty(acc.getUsername(), acc.isOffline() ? "n/a" : acc
                    .getPassword()));
            properties.store(stream, "Syntax = \"username=password\", comments begin with #.");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void addAccount(Account acc) {
        try {
            this.contents.add(acc);
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAccount(Account acc) {
        try {
            this.contents.remove(acc);
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<Account> getAccountByUsername(String username) {
        return this.contents.stream().filter(acc -> username.equals(username)).findAny();
    }

    @Override
    public void startup() {
        this.contents = new ArrayList<>();
    }

}
