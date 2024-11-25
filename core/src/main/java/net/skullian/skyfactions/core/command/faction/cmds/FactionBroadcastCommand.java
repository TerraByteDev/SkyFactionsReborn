package net.skullian.skyfactions.core.command.faction.cmds;

import net.skullian.skyfactions.core.api.FactionAPI;
import net.skullian.skyfactions.core.command.CommandTemplate;
import net.skullian.skyfactions.core.command.CommandsUtility;
import net.skullian.skyfactions.core.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.core.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

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
    @Permission(value = {"skyfactions.faction.broadcast", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player,
            @Argument(value = "message") @Greedy String message
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                if (!Settings.FACTION_CREATE_BROADCAST_PERMISSIONS.getList().contains(faction.getRankType(player.getUniqueId()).getRankValue())) {
                    Messages.FACTION_ACTION_DENY.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                } else {
                    if (FactionAPI.hasValidName(player, message)) {
                        faction.createAuditLog(player.getUniqueId(), AuditLogType.BROADCAST_CREATE, "player_name", player.getName());
                        faction.createBroadcast(player, message);
                    }
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
