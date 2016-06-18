package pw.haze.client.management.preset;

import pw.haze.client.management.value.Value;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * |> Author: haze
 * |> Since: 4/2/16
 */
public class Preset {
    private Map<Value<?>, String> map;
    private String name;

    public Preset(String name) {
        this.map = new LinkedHashMap<>();
        this.name = name;
    }

    public Preset(Map<Value<?>, String> value, String name) {
        this.map = value;
        this.name = name;
    }

    public Map<Value<?>, String> getMap() {
        return map;
    }

    public void setMap(Map<Value<?>, String> map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void apply() {
        for (Map.Entry<Value<?>, String> entry : this.getMap().entrySet()) {
            Value v = entry.getKey();
            String data = entry.getValue();
            if (Float.class.isAssignableFrom(v.get().getClass())) {
                v.setValue(Float.parseFloat(data));
            } else if (Integer.class.isAssignableFrom(v.get().getClass())) {
                v.setValue(Integer.parseInt(data));
            } else if (Boolean.class.isAssignableFrom(v.get().getClass())) {
                v.setValue(Boolean.parseBoolean(data));
            } else {
                v.setValue(data);
            }
        }
    }

}
