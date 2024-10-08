package net.skullian.torrent.skyfactions.command.faction.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.api.NotificationAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionRequestJoinCommand extends CommandTemplate {
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

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;
        if (args.length <= 1) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
            return;
        }

        FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "check if you were in a Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player);
                return;
            }

            String factionName = args[1];
            FactionAPI.getFaction(factionName).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorHandler.handleError(player, "check the Faction", "SQL_FACTION_GET", throwable);
                    return;
                } else if (faction == null) {
                    Messages.FACTION_NOT_FOUND.send(player, "%name%", factionName);
                    return;
                } else if (faction.getAllMembers().contains(Bukkit.getOfflinePlayer(player.getUniqueId()))) {
                    Messages.JOIN_REQUEST_SAME_FACTION.send(player);
                    return;
                } else {
                    SkyFactionsReborn.db.hasJoinRequest(player).whenComplete((hasJoinRequest, exc) -> {
                        if (exc != null) {
                            ErrorHandler.handleError(player, "check if you have a join request", "SQL_JOIN_REQUEST_GET", exc);
                            return;
                        }

                        if (hasJoinRequest) {
                            Messages.JOIN_REQUEST_ALREADY_EXISTS.send(player);
                            return;
                        }
                    });
                }

                faction.createJoinRequest(Bukkit.getOfflinePlayer(player.getUniqueId())).whenComplete((ignored, exc) -> {
                    if (exc != null) {
                        ErrorHandler.handleError(player, "create a join request", "SQL_JOIN_REQUEST_GET", exc);
                        return;
                    }

                    Messages.JOIN_REQUEST_CREATE_SUCCESS.send(player, "%faction_name%", factionName);
                    NotificationAPI.factionInviteStore.replace(factionName, (NotificationAPI.factionInviteStore.get(factionName) + 1));
                });
            });
        });

    }

    public static List<String> permissions = List.of("skyfactions.faction.requestjoin", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
