package net.skullian.torrent.skyfactions.command.faction.cmds;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;

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
            if (!FactionAPI.isInFaction(player)) {
                Messages.NOT_IN_FACTION.send(player);
                return;
            } else if (!FactionAPI.isModerator(player) || !FactionAPI.isOwner(player)) {
                Messages.PERMISSION_DENY.send(player);
                return;
            }

            Messages.MOTD_CHANGE_PROCESSING.send(player);

            // TODO: TEST!
            String message = args[1];
            if (!TextUtility.hasBlacklistedWords(player, message)) {
                Faction faction = FactionAPI.getFaction(player);
                if (faction != null) {
                    faction.updateMOTD(message);
                    Messages.MOTD_CHANGE_SUCCESS.send(player);
                }
            }


        } else if (args.length <= 1) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        }
    }

    @Override
    public String permission() {
        return "skyfactions.faction.motd";
    }
}
