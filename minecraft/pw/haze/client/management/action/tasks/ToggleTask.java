package pw.haze.client.management.action.tasks;

import pw.haze.client.management.action.Task;
import pw.haze.client.management.module.ToggleableModule;

/**
 * Created by Haze on 6/4/2015.
 */
public class ToggleTask extends Task {

    private ToggleableModule module;

    public ToggleTask(ToggleableModule module) {
        this.module = module;
    }

    @Override
    public void invoke() {
        module.toggle();
    }

    public ToggleableModule getModule() {
        return module;
    }

}
