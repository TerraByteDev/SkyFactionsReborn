package net.skullian.skyfactions.util;

import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;

public class ErrorHandler {

    public static void handleError(CommandSender sender, String operation, String debug, Throwable err) {
        err.printStackTrace();
        Messages.ERROR.send(sender, (sender instanceof Player ? ((Player) sender).locale() : Locale.of(Settings.DEFAULT_LANGUAGE.getString())), "%operation%", operation, "%debug%", debug);
    }

}
