package pw.haze.client.management.value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pw.haze.client.interfaces.Loadable;
import pw.haze.client.interfaces.Savable;
import pw.haze.client.management.MapManager;
import pw.haze.client.management.module.Module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Haze on 6/4/2015.
 */
public class ValueManager extends MapManager<ValueHelper, Value[]> implements Loadable, Savable {

    public void setValueList(Module module, Value[] newValues) {
        getMap().keySet().stream().filter(o -> module.getClass().isInstance(o)).forEach(o -> {
            getMap().remove(o);
            getMap().put(o, newValues);
        });
    }

    public Optional<Value[]> getValuesFromModule(Module m) {
        for (Map.Entry<ValueHelper, Value[]> entry : getMap().entrySet()) {
            if (m.getClass().isInstance(entry.getKey().getObj())) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    public Boolean doesValueExist(String name) {
        return this.map.values().stream().anyMatch(
                m -> Arrays.stream(m).anyMatch(v -> v.getName().equals(name))
        );
    }

    public Boolean doesValueExist(Module m, String name) {
        for (ValueHelper vh : this.map.keySet()) {
            if (vh.getObj() instanceof Module) {
                for (Value v : this.map.get(vh)) {
                    if (v.getName().equals(name)) return true;
                }
            }
        }
        return false;
    }

    public Optional<Value> getValueFromModule(Module moodule, String name) {
        if (doesValueExist(moodule, name)) {
            for (ValueHelper vh : this.map.keySet()) {
                if (vh.getObj() instanceof Module) {
                    for (Value v : this.map.get(vh)) {
                        if (v.getName().equals(name)) return Optional.of(v);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Value> getValue(String name) {
        if (doesValueExist(name)) {
            for (Value[] values : this.map.values()) {
                for (Value v : values) {
                    if (v.getName().equals(name)) return Optional.of(v);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Value> getValueFor(Module module, String valueName) {
        Optional<Value[]> optionalValues = this.getValuesFromModule(module);
        if (optionalValues.isPresent()) {
            for (Value v : optionalValues.get()) {
                if (v.getName().equals(valueName)) return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    @Override
    public void startup() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public void load() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(getFile("misc", "json")))) {
            Gson gson = new GsonBuilder().create();
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                Optional<Value> val = getValue(entry.getKey());
                if (val.isPresent()) {
                    Value value = val.get();
                    if (Float.class.isAssignableFrom(value.get().getClass())) {
                        value.setValue(entry.getValue().getAsFloat());
                    } else if (Integer.class.isAssignableFrom(value.get().getClass())) {
                        value.setValue(entry.getValue().getAsInt());
                    } else if (Boolean.class.isAssignableFrom(value.get().getClass())) {
                        value.setValue(entry.getValue().getAsBoolean());
                    } else {
                        value.setValue(entry.getValue().getAsString());
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void save() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile("misc", "json")))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject root = new JsonObject();
            this.getMap().entrySet().stream().filter(entry -> !(entry.getKey().getObj() instanceof Module)).forEach(entry -> {
                for (Value value : entry.getValue()) {
                    if (Float.class.isAssignableFrom(value.get().getClass())) {
                        root.addProperty(value.getName(), (Float) value.get());
                    } else if (Integer.class.isAssignableFrom(value.get().getClass())) {
                        root.addProperty(value.getName(), (Integer) value.get());
                    } else if (Boolean.class.isAssignableFrom(value.get().getClass())) {
                        root.addProperty(value.getName(), (Boolean) value.get());
                    } else {
                        root.addProperty(value.getName(), value.get().toString());
                    }
                }
            });
            writer.write(gson.toJson(root));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
