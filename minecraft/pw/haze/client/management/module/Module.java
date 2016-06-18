package pw.haze.client.management.module;

import pw.haze.client.Client;
import pw.haze.client.management.value.Value;
import pw.haze.client.management.value.ValueHelper;

/**
 * Created by Haze on 6/4/2015.
 */
public class Module {

    private String name, description;

    public Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void offerValues(Value[] values) {
        Client.getInstance().getValueManager().getMap().put(new ValueHelper(this.getName(), this), values);
    }

}
