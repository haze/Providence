package pw.haze.client.ui.tab;

import pw.haze.client.management.module.ToggleableModule;

public class ModuleContent {
    private ToggleableModule module;
    private int id;

    public ModuleContent(ToggleableModule module, int id) {
        this.module = module;
        this.id = id;

    }

    public ToggleableModule getModule() {
        return module;
    }

    public void setModule(ToggleableModule module) {
        this.module = module;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
