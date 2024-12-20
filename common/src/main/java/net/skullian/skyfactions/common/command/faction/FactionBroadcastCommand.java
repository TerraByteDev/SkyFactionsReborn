package net.skullian.skyfactions.common.command.faction;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionBroadcastCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "faction";
    }

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
            SkyUser player,
            @Argument(value = "message") @Greedy String message
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                if (!Settings.FACTION_CREATE_BROADCAST_PERMISSIONS.getList().contains(faction.getRankType(player.getUniqueId()).getRankValue())) {
                    Messages.FACTION_ACTION_DENY.send(player, locale);
                } else {
                    if (!TextUtility.containsBlockedPhrases(message)) {
                        faction.createAuditLog(player.getUniqueId(), AuditLogType.BROADCAST_CREATE, "player_name", player.getName());
                        faction.createBroadcast(player, message);
                    } else Messages.BLACKLISTED_PHRASE.send(player, locale);
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
