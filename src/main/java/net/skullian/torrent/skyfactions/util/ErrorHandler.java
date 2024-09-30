package net.skullian.torrent.skyfactions.util;

import net.skullian.torrent.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

public class ErrorHandler {

    public static void handleError(Player player, String operation, String debug, Throwable err) {
        err.printStackTrace();
        Messages.ERROR.send(player, "%operation%", operation, "%debug%", debug);
    }

}
