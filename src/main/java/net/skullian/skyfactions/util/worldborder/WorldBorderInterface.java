package net.skullian.skyfactions.util.worldborder;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WorldBorderInterface {

    default WorldBorder.BorderPos getCentre() {
        return centre();
    }

    default void setCenter(WorldBorder.BorderPos location) {
        centre(location);
    }

    default WorldBorder.BorderPos getMinimumPos() {
        return minimumPos();
    }

    default WorldBorder.BorderPos getMaximumPos() {
        return maximumPos();
    }

    default double getWorldBorderSize() {
        return worldBorderSize();
    }

    default void setWorldBorderSize(double size) {
        worldBorderSize(size);
    }

    void centre(WorldBorder.BorderPos location);

    WorldBorder.BorderPos centre();

    WorldBorder.BorderPos minimumPos();

    WorldBorder.BorderPos maximumPos();

    double worldBorderSize();

    void worldBorderSize(double radius);

    void update(BorderUpdateAction action, Player player);
}
