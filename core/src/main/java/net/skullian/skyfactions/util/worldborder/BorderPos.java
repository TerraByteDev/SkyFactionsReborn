package net.skullian.skyfactions.util.worldborder;

import org.bukkit.Location;

public record BorderPos(double x, double z) {
    public static BorderPos of(Location location) {
        return new BorderPos(location.getX(), location.getZ());
    }
}
