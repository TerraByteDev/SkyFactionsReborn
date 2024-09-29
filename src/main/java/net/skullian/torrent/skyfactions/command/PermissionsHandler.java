package net.skullian.torrent.skyfactions.command;

import net.skullian.torrent.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class PermissionsHandler {

    public static boolean hasPerm(Player player, List<String> node, boolean sendDeny) {
        for (String perm : node) {
            if (player.hasPermission(perm)) return true;
        }

        if (sendDeny) {
            Messages.PERMISSION_DENY.send(player);
        }
        return false;
    }
}
