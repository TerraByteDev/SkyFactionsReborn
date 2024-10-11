package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.FactionLeaveConfirmationUI;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        FactionAPI.getFaction(player).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction == null) {
                Messages.NOT_IN_FACTION.send(player);
                return;
            } else if (faction.isOwner(player)) {
                Messages.FACTION_OWNER_LEAVE_DENY.send(player);
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
