package net.skullian.skyfactions.paper.command.island.cmds;

import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.paper.api.SpigotRegionAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.config.types.Settings;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("island")
public class IslandTeleportCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDescription() {
        return "Teleport to your player island.";
    }

    @Override
    public String getSyntax() {
        return "/island teleport";
    }

    @Command("teleport")
    @Permission(value = {"skyfactions.island.teleport", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SpigotIslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (island == null) {
                Messages.NO_ISLAND.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
            } else if (SpigotRegionAPI.isLocationInRegion(player.getLocation(), "sfr_player_" + player.getUniqueId().toString())) {
                Messages.ALREADY_ON_ISLAND.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return;
            }

            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world != null) {
                SpigotFactionAPI.modifyDefenceOperation(SpigotFactionAPI.DefenceOperation.DISABLE, player);

                SpigotRegionAPI.teleportPlayerToLocation(player, island.getCenter(world));
                SpigotRegionAPI.modifyWorldBorder(player, island.getCenter(world), island.getSize()); // shift join border

                SpigotIslandAPI.onIslandLoad(player.getUniqueId());
            } else {
                Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "teleport you to your island", "debug", "WORLD_NOT_EXIST");
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.teleport", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
