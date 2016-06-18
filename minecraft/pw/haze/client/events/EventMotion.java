package pw.haze.client.events;

import pw.haze.event.Event;

/**
 * Created by Haze on 6/4/2015.
 */

public class EventMotion extends Event {

    private EnumMotion enumMotion;
    private float yaw, pitch;
    private double x, y, z, ox, oy, oz;
    private boolean onGround;

    public EventMotion(EnumMotion enumMotion, float yaw, float pitch, double x, double y, double z, double ox, double oy, double oz, boolean onGround) {
        this.enumMotion = enumMotion;
        this.yaw = yaw;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.onGround = onGround;
    }

    public boolean getOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
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

    public double getOx() {
        return ox;
    }

    public void setOx(double ox) {
        this.ox = ox;
    }

    public double getOy() {
        return oy;
    }

    public void setOy(double oy) {
        this.oy = oy;
    }

    public double getOz() {
        return oz;
    }

    public void setOz(double oz) {
        this.oz = oz;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public EnumMotion getEnumMotion() {
        return enumMotion;
    }

    public enum EnumMotion {
        PRE, POST
    }

}
