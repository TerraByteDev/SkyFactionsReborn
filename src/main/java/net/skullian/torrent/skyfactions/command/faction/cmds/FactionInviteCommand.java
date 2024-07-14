package net.skullian.torrent.skyfactions.command.faction.cmds;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
            Faction faction = FactionAPI.getFaction(player);
            if (faction == null) {
                Messages.NOT_IN_FACTION.send(player);
                return;
            }

            if (args[1].equalsIgnoreCase(player.getName())) {
                Messages.FACTION_INVITE_SELF_DENY.send(player);
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!target.hasPlayedBefore()) {
                Messages.UNKNOWN_PLAYER.send(player, "%player%", args[1]);
            } else if (faction.getAllMembers().contains(target)){
                Messages.FACTION_INVITE_IN_SAME_FACTION.send(player);
            } else {
                faction.createInvite(target, player);
                Messages.FACTION_INVITE_CREATE_SUCCESS.send(player, "%player_name%", target.getName());
            }
        }
    }

    @Override
    public String permission() {
        return "skyfactions.faction.invite";
    }
}
