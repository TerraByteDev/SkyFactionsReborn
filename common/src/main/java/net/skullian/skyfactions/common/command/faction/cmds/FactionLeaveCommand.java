package net.skullian.skyfactions.common.command.faction.cmds;

import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.gui.screens.confirmation.FactionLeaveConfirmationUI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionLeaveCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave the Faction you are currently in.";
    }

    @Override
    public String getSyntax() {
        return "/faction leave";
    }

    @Command("leave")
    @Permission(value = {"skyfactions.faction.leave", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return;
            }

            FactionLeaveConfirmationUI.promptPlayer(player);
        });

    }

    public static List<String> permissions = List.of("skyfactions.faction.leave", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
