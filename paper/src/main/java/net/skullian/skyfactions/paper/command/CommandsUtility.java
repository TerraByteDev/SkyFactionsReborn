package net.skullian.skyfactions.paper.command;

import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandsUtility {
    public static boolean hasPerm(Player player, List<String> node, boolean sendDeny) {
        for (String perm : node) {
            if (player.hasPermission(perm)) return true;
        }

        if (sendDeny) {
            Messages.PERMISSION_DENY.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
        }
        return false;
    }
}
