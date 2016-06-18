package pw.haze.client.events;

import net.minecraft.client.entity.AbstractClientPlayer;
import pw.haze.event.Event;

/**
 * |> Author: haze
 * |> Since: 3/18/16
 */
public class EventPlayerRender extends Event {

    private EnumSate state;
    private AbstractClientPlayer player;

    public EventPlayerRender(EnumSate state, AbstractClientPlayer player) {
        this.state = state;
        this.player = player;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public EnumSate getState() {
        return state;
    }

    public void setState(EnumSate state) {
        this.state = state;
    }

    public enum EnumSate {
        PRE, POST
    }
}
