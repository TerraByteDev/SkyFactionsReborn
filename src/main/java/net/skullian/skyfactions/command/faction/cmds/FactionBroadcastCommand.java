package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionBroadcastCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "broadcast";
    }

    @Override
    public String getDescription() {
        return "Create a broadcast to all online Faction members.";
    }

    @Override
    public String getSyntax() {
        return "/faction broadcast <message>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        if (args.length == 1) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        } else if (args.length > 1) {

            FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (faction != null) {
                    StringBuilder msg = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        msg.append(args[i]).append(" ");
                    }

                    if (faction.isOwner(player) || faction.isAdmin(player) || faction.isModerator(player)) {
                        String message = msg.toString().trim();
                        if (FactionAPI.hasValidName(player, message)) {
                            faction.createAuditLog(player.getUniqueId(), AuditLogType.BROADCAST_CREATE, "%player_name%", player.getName());
                            faction.createBroadcast(player, message);
                        }

                    } else {
                        Messages.FACTION_ACTION_DENY.send(player);
                    }

                }
            });
        }

    }

    public static List<String> permissions = List.of("skyfactions.faction.broadcast", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
