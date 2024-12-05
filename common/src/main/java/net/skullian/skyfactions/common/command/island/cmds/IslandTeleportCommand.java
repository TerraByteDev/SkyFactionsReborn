package net.skullian.skyfactions.common.command.island.cmds;

import net.skullian.skyfactions.common.api.FactionAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("island")
public class IslandTeleportCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "island";
    }

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
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (island == null) {
                Messages.NO_ISLAND.send(player, locale);
                return;
            } else if (SkyApi.getInstance().getRegionAPI().isLocationInRegion(player.getLocation(), "sfr_player_" + player.getUniqueId().toString())) {
                Messages.ALREADY_ON_ISLAND.send(player, locale);
                return;
            }

            String world = Settings.ISLAND_PLAYER_WORLD.getString();
            if (SkyApi.getInstance().getRegionAPI().worldExists(world)) {
                SkyApi.getInstance().getFactionAPI().modifyDefenceOperation(FactionAPI.DefenceOperation.DISABLE, player);

                player.teleport(island.getCenter(world));
                SkyApi.getInstance().getRegionAPI().modifyWorldBorder(player, island.getCenter(world), island.getSize()); // shift world border

                SkyApi.getInstance().getIslandAPI().onIslandLoad(player);
            } else {
                Messages.ERROR.send(player, locale, "operation", "teleport you to your island", "debug", "WORLD_NOT_EXIST");
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.teleport", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
