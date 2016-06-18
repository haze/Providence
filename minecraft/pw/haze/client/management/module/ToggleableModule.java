package pw.haze.client.management.module;

import pw.haze.client.Client;
import pw.haze.client.events.EventModuleToggle;
import pw.haze.client.management.action.Action;
import pw.haze.client.management.action.Task;
import pw.haze.client.management.action.tasks.ToggleTask;
import pw.haze.client.management.module.interfaces.Toggleable;
import pw.haze.event.EventManager;

/**
 * Created by Haze on 6/4/2015.
 */
public class ToggleableModule extends Module implements Toggleable {

    private String key;
    private Category category;
    private boolean registered, state, visible = true;

    public ToggleableModule(String name, String description, String key, Category category) {
        super(name, description);
        this.key = key;
        this.category = category;
        Client.getInstance().getActionManager().getMap().put(new Action[]{Action.KEY_PRESS}, new Task[]{new ToggleTask(this)});
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isRunning() {
        return state;
    }

    public void setState(Boolean par1Bool, Boolean fireToggle, Boolean save) {
        if (fireToggle) {
            EventModuleToggle toggle = new EventModuleToggle();
            EventManager.getInstance().fire(toggle);
            if (toggle.getCancelled()) {
                return;
            }
        }
        if (save) {
            try {
                Client.getInstance().getModuleManager().save();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        this.state = par1Bool;
        if (state) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void setState(Boolean par1Bool) {
        setState(par1Bool, true, false);
    }

    @Override
    public void onEnable() {
        EventManager.getInstance().registerAll(this);
    }

    @Override
    public void onDisable() {
        EventManager.getInstance().unregisterAll(this);
    }

    @Override
    public void toggle() {
        setState(!state, true, true);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String par1Str) {
        this.key = par1Str;
    }
}
