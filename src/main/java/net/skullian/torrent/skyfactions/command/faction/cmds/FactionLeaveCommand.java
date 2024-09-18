package net.skullian.torrent.skyfactions.command.faction.cmds;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.gui.FactionLeaveConfirmationUI;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionLeaveCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave the Faction you are currently in.";
    }

    @Override
    public String getSyntax() {
        return "/faction leave";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        Faction faction = FactionAPI.getFaction(player);
        if (faction == null) {
            Messages.NOT_IN_FACTION.send(player);
            return;
        } else if (faction.isOwner(player)) {
            Messages.FACTION_OWNER_LEAVE_DENY.send(player);
            return;
        }

        FactionLeaveConfirmationUI.promptPlayer(player);

    }

    @Override
    public List<String> permission() {
        return List.of( "skyfactions.faction.leave", "skyfactions.faction", "skyfactions.player");
    }
}
