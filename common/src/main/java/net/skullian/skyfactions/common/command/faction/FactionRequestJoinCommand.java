package net.skullian.skyfactions.common.command.faction;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionRequestJoinCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "faction";
    }

    @Override
    public String getName() {
        return "requestjoin";
    }

    @Override
    public String getDescription() {
        return "Request to join another faction.";
    }

    @Override
    public String getSyntax() {
        return "/faction requestjoin <faction name>";
    }

    @Command("requestjoin <factionName>")
    @Permission(value = {"skyfactions.faction.requestjoin", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "factionName") String factionName
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        SkyApi.getInstance().getFactionAPI().isInFaction(player.getUniqueId()).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "check if you were in a Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player, locale);
                return;
            }

            SkyApi.getInstance().getFactionAPI().getFaction(factionName).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "check the Faction", "SQL_FACTION_GET", throwable);
                    return;
                } else if (faction == null) {
                    Messages.FACTION_NOT_FOUND.send(player, locale, "name", factionName);
                    return;
                } else if (faction.getAllMembers().contains(player)) {
                    Messages.JOIN_REQUEST_SAME_FACTION.send(player, locale);
                    return;
                } else if (faction.isPlayerBanned(player)) {
                    Messages.FACTION_INVITE_REQUEST_JOIN_BANNED.send(player, locale);
                    return;
                }

                JoinRequestData fetchedRequest = faction.getPlayerJoinRequest(player);
                if (fetchedRequest != null) {
                    Messages.JOIN_REQUEST_ALREADY_EXISTS.send(player, locale);
                } else {
                    InviteData joinRequest = new InviteData(
                            player,
                            null,
                            faction.getName(),
                            "incoming",
                            System.currentTimeMillis()
                    );

                    faction.createJoinRequest(joinRequest);

                    Messages.JOIN_REQUEST_CREATE_SUCCESS.send(player, locale, "faction_name", factionName);
                    SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().replace(factionName, (SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().get(factionName) + 1));
                }
            });
        });

    }

    public static List<String> permissions = List.of("skyfactions.faction.requestjoin", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
