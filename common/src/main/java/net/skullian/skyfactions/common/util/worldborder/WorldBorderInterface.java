package net.skullian.skyfactions.common.util.worldborder;

import org.bukkit.entity.Player;

public interface WorldBorderInterface {

    default BorderPos getCentre() {
        return centre();
    }

    default void setCenter(BorderPos location) {
        centre(location);
    }

    default BorderPos getMinimumPos() {
        return minimumPos();
    }

    default BorderPos getMaximumPos() {
        return maximumPos();
    }

    default double getWorldBorderSize() {
        return worldBorderSize();
    }

    default void setWorldBorderSize(double size) {
        worldBorderSize(size);
    }

    void centre(BorderPos location);
    BorderPos centre();
    BorderPos minimumPos();
    BorderPos maximumPos();
    double worldBorderSize();
    void worldBorderSize(double radius);
    void update(BorderUpdateAction action, Player player);
}
