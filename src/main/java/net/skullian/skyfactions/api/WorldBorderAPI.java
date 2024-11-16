package net.skullian.skyfactions.api;

import net.skullian.skyfactions.util.worldborder.BorderAPI;
import net.skullian.skyfactions.util.worldborder.BorderUpdateAction;
import net.skullian.skyfactions.util.worldborder.WorldBorder;
import net.skullian.skyfactions.util.worldborder.WorldBorderInterface;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Function;

public class WorldBorderAPI implements BorderAPI {

    private final Function<World, WorldBorderInterface> worldBorders;
    private final Function<Player, WorldBorderInterface> playerBorders;

    public WorldBorderAPI(Function<Player, WorldBorderInterface> playerBorders, Function<World, WorldBorderInterface> worldBorders) {
        this.worldBorders = worldBorders;
        this.playerBorders = playerBorders;
    }

    @Override
    public void resetBorder(Player player) {
        worldBorders.apply(player.getWorld()).update(BorderUpdateAction.SET, player);
    }

    @Override
    public void setWorldBorder(Player player, double radius, WorldBorder.BorderPos location) {
        WorldBorderInterface border = playerBorders.apply(player);
        border.setWorldBorderSize(radius);
        border.setCenter(location);
        border.update(BorderUpdateAction.MODIFY_SIZE, player);
        border.update(BorderUpdateAction.MODIFY_CENTER, player);
    }
}
