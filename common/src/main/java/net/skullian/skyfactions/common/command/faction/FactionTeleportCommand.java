package net.skullian.skyfactions.common.command.faction;

import net.skullian.skyfactions.common.api.FactionAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionTeleportCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "faction";
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDescription() {
        return "Teleport to your faction's island.";
    }

    @Override
    public String getSyntax() {
        return "/faction teleport";
    }

    @Command("teleport")
    @Permission(value = {"skyfactions.faction.teleport", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, locale);
                return;
            } else if (SkyApi.getInstance().getRegionAPI().isLocationInRegion(player.getLocation(), "sfr_faction_" + faction.getName())) {
                Messages.ALREADY_ON_ISLAND.send(player, locale);
                return;
            }

            SkyApi.getInstance().getIslandAPI().modifyDefenceOperation(FactionAPI.DefenceOperation.DISABLE, player);

            SkyApi.getInstance().getFactionAPI().teleportToFactionIsland(player, faction);
            SkyApi.getInstance().getFactionAPI().handleFactionWorldBorder(player, faction.getIsland());
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.teleport", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
