package pw.haze.client.events;

import net.minecraft.network.Packet;
import pw.haze.event.Event;

/**
 * Created by Haze on 6/26/2015.
 */
public class EventPacketReceive extends Event {
    private Packet packet;

    public EventPacketReceive(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
