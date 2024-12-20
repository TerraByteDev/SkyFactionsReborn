package net.skullian.skyfactions.common.command;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;

import java.util.List;

public class CommandsUtility {
    public static boolean hasPerm(SkyUser player, List<String> node, boolean sendDeny) {
        for (String perm : node) {
            if (player.hasPermission(perm)) return true;
        }

        if (sendDeny) {
            Messages.PERMISSION_DENY.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
        }
        return false;
    }
}
