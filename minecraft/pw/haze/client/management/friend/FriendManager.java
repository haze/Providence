package pw.haze.client.management.friend;

import pw.haze.client.interfaces.Loadable;
import pw.haze.client.interfaces.Savable;
import pw.haze.client.management.ListManager;
import pw.haze.client.management.command.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by Haze on 6/4/2015.
 */
public class FriendManager extends ListManager<Friend> implements Savable, Loadable {
    public FriendManager() {
        super("Friends");
    }

    public Optional<Friend> getFriendUsername(String username) {
        return this.contents.stream()
                .filter(friend -> friend.getUsername().equalsIgnoreCase(username))
                .findAny();
    }

    public Optional<Friend> getFriendAlias(String username) {
        return this.contents.stream()
                .filter(friend -> friend.getAlias().equalsIgnoreCase(username))
                .findAny();
    }


    @Command({"friendrem", "frm"})
    public String friendRemove(String name) {
        try {
            if (getFriendUsername(name).isPresent()) {
                getContents().remove(getFriendUsername(name).get());
                return String.format("Successfully removed friend \247a%s", name);
            } else if (getFriendAlias(name).isPresent()) {
                getContents().remove(getFriendAlias(name).get());
                return String.format("Successfully removed friend \247a%s", name);
            } else {
                return String.format("Cannot find friend \247a%s", name);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return "Failed to save FriendsList.";
        }
    }


    @Command({"friendadd", "fra"})
    public String friendAdd(String username, Optional<String> alias) {
        try {
            if (getFriendUsername(username).isPresent()) {
                return String.format("Friend %s actually exists", username);
            }
            if (alias.isPresent()) {
                getContents().add(new Friend(username, alias.get()));
                save();
                return String.format("Successfully addded \247a%s \247ras \247a%s\247r", username, alias.get());
            } else {
                getContents().add(new Friend(username, username));
                save();
                return String.format("Successfully added \247a%s", username);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return "Failed to save FriendsList.";
        }
    }

    @Override
    public void startup() {
        this.contents = new ArrayList<>();
    }

    @Override
    public void load() throws Exception {
        Properties properties;
        try (BufferedReader reader = new BufferedReader(new FileReader(getFile("properties")))) {
            properties = new Properties();
            properties.load(reader);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                getContents().add(new Friend(entry.getKey().toString(), entry.getValue().toString()));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void save() throws Exception {
        Properties properties;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile("properties")))) {
            properties = new Properties();
            for (Friend f : getContents()) {
                properties.put(f.getUsername(), f.getAlias());
            }
            properties.store(writer, "This is the friends file. ex (username=alias)");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
