package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionMOTDCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "motd";
    }

    @Override
    public String getDescription() {
        return "Set your faction's MOTD (Message of the Day).";
    }

    @Override
    public String getSyntax() {
        return "/faction motd <motd>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        if (args.length > 1) {
            FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                }

                if (faction == null) {
                    Messages.NOT_IN_FACTION.send(player);
                    return;
                } else if (!faction.isOwner(player) || !faction.isModerator(player)) {
                    Messages.PERMISSION_DENY.send(player);
                    return;
                }

                Messages.MOTD_CHANGE_PROCESSING.send(player);

                StringBuilder msg = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    msg.append(args[i]).append(" ");
                }
                String message = msg.toString();


                if (!TextUtility.hasBlacklistedWords(player, message)) {
                    faction.updateMOTD(message, player);
                    Messages.MOTD_CHANGE_SUCCESS.send(player);
                }
            });
        } else if (args.length <= 1) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        }
    }

    public static List<String> permissions = List.of("skyfactions.faction.motd", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
