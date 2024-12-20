package net.skullian.skyfactions.common.util.worldborder;

import net.skullian.skyfactions.common.util.SkyLocation;

public record BorderPos(double x, double z) {
    public static BorderPos of(SkyLocation location) {
        return new BorderPos(location.getX(), location.getZ());
    }
}
