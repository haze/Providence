package pw.haze.client.management.value;

import pw.haze.client.Client;

/**
 * Created by Haze on 6/4/2015.
 */
public class Value<T> {

    private String name;
    private T value;

    public Value(T defValue, String name) {
        this.value = defValue;
        this.name = name;
    }

    public T get() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        try {
            Client.getInstance().getValueManager().save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public Class getTypeClass() {
        return this.value.getClass();
    }

}
