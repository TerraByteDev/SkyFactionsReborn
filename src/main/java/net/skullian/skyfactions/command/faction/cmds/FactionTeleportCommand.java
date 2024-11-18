package net.skullian.skyfactions.command.faction.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RegionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.PlayerSource;

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
        String locale = PlayerHandler.getLocale(player.getUniqueId());

        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, locale);
                return;
            } else if (RegionAPI.isLocationInRegion(player.getLocation(), "SFR_FACTION_" + faction.getName())) {
                Messages.ALREADY_ON_ISLAND.send(player, locale);
                return;
            }

            IslandAPI.modifyDefenceOperation(FactionAPI.DefenceOperation.DISABLE, player.getUniqueId());

            FactionAPI.teleportToFactionIsland(player, faction);
            FactionAPI.handleFactionWorldBorder(player, faction.getIsland());
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.teleport", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
