package pw.haze.client.management;

import pw.haze.client.Client;

import java.io.File;
import java.util.List;

/**
 * Created by Haze on 6/4/2015.
 */
public abstract class ListManager<T> {

    protected List<T> contents;
    private String name;

    public ListManager(String name) {
        this.name = name;
    }

    public List<T> getContents() {
        return this.contents;
    }

    public abstract void startup();

    public File getFile(String extension) {
        return new File(Client.PROVIDENCE_FILE + "/" + name + String.format(".%s", extension));
    }

}
