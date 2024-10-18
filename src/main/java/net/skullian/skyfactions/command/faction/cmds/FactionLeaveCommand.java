package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.FactionLeaveConfirmationUI;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

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
    public void perform(
            CommandSender sender
    ) {
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
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
