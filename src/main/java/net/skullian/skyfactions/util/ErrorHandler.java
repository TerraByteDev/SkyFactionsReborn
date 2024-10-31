package net.skullian.skyfactions.util;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.event.PlayerHandler;

public class ErrorHandler {

    public static void handleError(CommandSender sender, String operation, String debug, Throwable err) {
        err.printStackTrace();
        Messages.ERROR.send(sender, sender instanceof Player ? PlayerHandler.getLocale(((Player) sender).getUniqueId()) : Messages.getDefaulLocale(), "%operation%", operation, "%debug%", debug);
    }

}
