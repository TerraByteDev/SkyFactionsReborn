package net.skullian.skyfactions.command.faction.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
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

    @Command("requestjoin <factionName>")
    @Permission(value = {"skyfactions.faction.requestjoin", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack,
            @Argument(value = "factionName") String factionName
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "check if you were in a Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            }

            FactionAPI.getFaction(factionName).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "check the Faction", "SQL_FACTION_GET", throwable);
                    return;
                } else if (faction == null) {
                    Messages.FACTION_NOT_FOUND.send(player, PlayerHandler.getLocale(player.getUniqueId()), "name", factionName);
                    return;
                } else if (faction.getAllMembers().contains(Bukkit.getOfflinePlayer(player.getUniqueId()))) {
                    Messages.JOIN_REQUEST_SAME_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                    return;
                } else {
                    SkyFactionsReborn.databaseManager.factionInvitesManager.hasJoinRequest(player.getUniqueId()).whenComplete((hasJoinRequest, exc) -> {
                        if (exc != null) {
                            ErrorUtil.handleError(player, "check if you have a join request", "SQL_JOIN_REQUEST_GET", exc);
                            return;
                        }

                        if (hasJoinRequest) {
                            Messages.JOIN_REQUEST_ALREADY_EXISTS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                            return;
                        }
                    });
                }

                faction.createJoinRequest(Bukkit.getOfflinePlayer(player.getUniqueId())).whenComplete((ignored, exc) -> {
                    if (exc != null) {
                        ErrorUtil.handleError(player, "create a join request", "SQL_JOIN_REQUEST_GET", exc);
                        return;
                    }

                    Messages.JOIN_REQUEST_CREATE_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "faction_name", factionName);
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
