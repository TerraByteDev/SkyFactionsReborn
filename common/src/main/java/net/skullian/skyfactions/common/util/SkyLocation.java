package net.skullian.skyfactions.common.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkyLocation implements Cloneable {

    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public SkyLocation(String worldName, double x, double y, double z) {
        this(worldName, x, y, z, 0, 0);
    }

    public SkyLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public SkyLocation add(SkyLocation location) {
        this.x += location.getX();
        this.y += location.getY();
        this.z += location.getZ();

        return this;
    }

    public SkyLocation add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public SkyLocation subtract(SkyLocation location) {
        this.x -= location.getX();
        this.y -= location.getY();
        this.z -= location.getZ();

        return this;
    }

    public SkyLocation subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public SkyLocation multiply(double multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
        this.z *= multiplier;

        return this;
    }

    @Override
    public SkyLocation clone() {
        try {
            return (SkyLocation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public int getBlockX() {
        return toBlockLocation(x);
    }

    public int getBlockY() {
        return toBlockLocation(y);
    }

    public int getBlockZ() {
        return toBlockLocation(z);
    }

    public static int toBlockLocation(double loc) {
        final int floor = (int) loc;
        return floor == loc ? floor : floor - (int) (Double.doubleToRawLongBits(loc) >>> 63);
    }
}
