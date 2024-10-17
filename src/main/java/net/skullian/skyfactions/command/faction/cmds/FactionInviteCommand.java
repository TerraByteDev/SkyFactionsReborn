package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.db.InviteData;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionInviteCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invite a player to your Faction.";
    }

    @Override
    public String getSyntax() {
        return "/faction invite <player>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        if (args.length == 1) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        } else if (args.length > 1) {
            String name = args[1];
            FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (faction == null) {
                    Messages.NOT_IN_FACTION.send(player);
                    return;
                } else if (name.equalsIgnoreCase(player.getName())) {
                    Messages.FACTION_INVITE_SELF_DENY.send(player);
                    return;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (!target.hasPlayedBefore()) {
                    Messages.UNKNOWN_PLAYER.send(player, "%player%", name);

                } else if (faction.getAllMembers().contains(target)) {
                    Messages.FACTION_INVITE_IN_SAME_FACTION.send(player);

                } else {
                    faction.getOutgoingInvites().whenComplete((invites, throwable) -> {
                        if (throwable != null) {
                            ErrorHandler.handleError(player, "get Faction invites", "SQL_FACTION_GET", throwable);
                            return;
                        }

                        for (InviteData invite : invites) {
                            if (invite.getPlayer().getName().equalsIgnoreCase(target.getName())) {
                                Messages.FACTION_INVITE_DUPLICATE.send(player);
                                return;
                            }
                        }
                    });

                    faction.createInvite(target, player);
                    Messages.FACTION_INVITE_CREATE_SUCCESS.send(player, "%player_name%", target.getName());
                }
            });
        }
    }

    public static List<String> permissions = List.of("skyfactions.faction.invite", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
