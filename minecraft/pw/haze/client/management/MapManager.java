package pw.haze.client.management;

import pw.haze.client.Client;

import java.io.File;
import java.util.Map;

/**
 * Created by Haze on 6/4/2015.
 */
public abstract class MapManager<K, V> {

    protected Map<K, V> map;

    public Map<K, V> getMap() {
        return map;
    }

    public abstract void startup();

    public File getFile(String fileName, String extension) {
        return new File(Client.PROVIDENCE_FILE + "/" + fileName + String.format(".%s", extension));
    }

}
