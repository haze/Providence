package pw.haze.client.events;

import pw.haze.event.Event;

/**
 * Created by Haze on 6/25/2015.
 */
public class EventVelocity extends Event {
    private double x, y, z;

    public EventVelocity(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
