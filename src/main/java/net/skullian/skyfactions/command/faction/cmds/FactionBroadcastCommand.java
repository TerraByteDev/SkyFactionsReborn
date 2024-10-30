package net.skullian.skyfactions.command.faction.cmds;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.AuditLogType;
import net.skullian.skyfactions.util.ErrorHandler;

@Command("faction")
public class FactionBroadcastCommand extends CommandTemplate {

    FactionCommandHandler handler;

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

    @Command("broadcast <message>")
    @Permission(value = { "skyfactions.faction.broadcast", "skyfactions.faction" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack,
            @Argument(value = "message") @Greedy String message
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                if (faction.isOwner(player) || faction.isAdmin(player) || faction.isModerator(player)) {
                    if (FactionAPI.hasValidName(player, message)) {
                        faction.createAuditLog(player.getUniqueId(), AuditLogType.BROADCAST_CREATE, "%player_name%", player.getName());
                        faction.createBroadcast(player, message);
                    }

                } else {
                    Messages.FACTION_ACTION_DENY.send(player, player.locale());
                }
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.broadcast", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
