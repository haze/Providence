package pw.haze.client.events;

import net.minecraft.entity.EntityLivingBase;
import pw.haze.event.Event;

/**
 * |> Author: haze
 * |> Since: 3/18/16
 */
public class EventRenderNametag extends Event {
    private EntityLivingBase livingbase;

    public EventRenderNametag(EntityLivingBase livingbase) {

        this.livingbase = livingbase;
    }

    public EntityLivingBase getLivingbase() {
        return livingbase;
    }

    public void setLivingbase(EntityLivingBase livingbase) {
        this.livingbase = livingbase;
    }
}
