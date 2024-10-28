package net.skullian.skyfactions.util;

import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.command.CommandSender;

public class ErrorHandler {

    public static void handleError(CommandSender player, String operation, String debug, Throwable err) {
        err.printStackTrace();
        Messages.ERROR.send(player, "%operation%", operation, "%debug%", debug);
    }

}
