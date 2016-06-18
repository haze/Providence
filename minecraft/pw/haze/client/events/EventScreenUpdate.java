package pw.haze.client.events;

import net.minecraft.client.gui.GuiScreen;
import pw.haze.event.Event;

/**
 * |> Author: haze
 * |> Since: 4/2/16
 */
public class EventScreenUpdate extends Event {
    private GuiScreen old, updated;

    public EventScreenUpdate(GuiScreen old, GuiScreen updated) {
        this.old = old;
        this.updated = updated;
    }

    public GuiScreen getOld() {
        return old;
    }

    public GuiScreen getUpdated() {
        return updated;
    }
}
