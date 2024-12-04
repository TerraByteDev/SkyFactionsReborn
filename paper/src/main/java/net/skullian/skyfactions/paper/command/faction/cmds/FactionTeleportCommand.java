package net.skullian.skyfactions.paper.command.faction.cmds;

import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.paper.api.SpigotRegionAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionTeleportCommand extends CommandTemplate {
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
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SpigotPlayerAPI.getLocale(player.getUniqueId());

        SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, locale);
                return;
            } else if (SpigotRegionAPI.isLocationInRegion(player.getLocation(), "sfr_faction_" + faction.getName())) {
                Messages.ALREADY_ON_ISLAND.send(player, locale);
                return;
            }

            SpigotIslandAPI.modifyDefenceOperation(SpigotFactionAPI.DefenceOperation.DISABLE, player.getUniqueId());

            SpigotFactionAPI.teleportToFactionIsland(player, faction);
            SpigotFactionAPI.handleFactionWorldBorder(player, faction.getIsland());
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.teleport", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
