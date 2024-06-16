package net.skullian.torrent.skyfactions.command;

import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.entity.Player;

public class PermissionsHandler {

    public static boolean hasPerm(Player player, String node, boolean sendDeny) {
        if (player.hasPermission(node)) return true;

        if (sendDeny) {
            Messages.PERMISSION_DENY.send(player);
        }
        return false;
    }
}
