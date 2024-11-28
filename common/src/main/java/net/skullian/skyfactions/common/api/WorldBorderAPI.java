package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.worldborder.BorderAPI;
import net.skullian.skyfactions.common.util.worldborder.BorderPos;
import net.skullian.skyfactions.common.util.worldborder.BorderUpdateAction;
import net.skullian.skyfactions.common.util.worldborder.WorldBorderInterface;

import java.util.function.Function;

public class WorldBorderAPI implements BorderAPI {

    private final Function<String, WorldBorderInterface> worldBorders;
    private final Function<SkyUser, WorldBorderInterface> playerBorders;

    public WorldBorderAPI(Function<SkyUser, WorldBorderInterface> playerBorders, Function<String, WorldBorderInterface> worldBorders) {
        this.worldBorders = worldBorders;
        this.playerBorders = playerBorders;
    }

    @Override
    public void resetBorder(SkyUser player) {
        worldBorders.apply(player.getWorld()).update(BorderUpdateAction.SET, player);
    }

    @Override
    public void setWorldBorder(SkyUser player, double radius, BorderPos location) {
        WorldBorderInterface border = playerBorders.apply(player);
        border.setWorldBorderSize(radius);
        border.setCenter(location);
        border.update(BorderUpdateAction.MODIFY_SIZE, player);
        border.update(BorderUpdateAction.MODIFY_CENTER, player);
    }
}
