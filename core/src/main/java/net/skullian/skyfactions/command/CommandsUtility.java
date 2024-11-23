package net.skullian.skyfactions.command;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandsUtility {
    public static boolean hasPerm(Player player, List<String> node, boolean sendDeny) {
        for (String perm : node) {
            if (player.hasPermission(perm)) return true;
        }

        if (sendDeny) {
            Messages.PERMISSION_DENY.send(player, PlayerAPI.getLocale(player.getUniqueId()));
        }
        return false;
    }
}
