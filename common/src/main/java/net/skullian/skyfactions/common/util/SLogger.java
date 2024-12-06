package net.skullian.skyfactions.common.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skullian.skyfactions.common.api.SkyApi;

public class SLogger {

    public static void info(Object message, Object... args) {
        Component infoLog = MiniMessage.miniMessage().deserialize("<#4294ed>" + format(message, args) + "<#4294ed>");
        SkyApi.getInstance().getConsoleAudience().sendMessage(infoLog);
    }

    public static void warn(Object message, Object... args) {
        Component warnLog = MiniMessage.miniMessage().deserialize("<#f28f24>" + format(message, args) + "<#f28f24>");
        SkyApi.getInstance().getConsoleAudience().sendMessage(warnLog);
    }

    public static void fatal(Object message, Object... args) {
        Component fatalLog = MiniMessage.miniMessage().deserialize("<#e73f38>" + format(message, args) + "<#e73f38>");
        SkyApi.getInstance().getConsoleAudience().sendMessage(fatalLog);
    }

    private static String format(Object message, Object... args) {
        if (message == null) {
            return null;
        }
        String formattedMessage = message.toString();
        for (Object arg : args) {
            formattedMessage = formattedMessage.replaceFirst("\\{\\}", arg.toString());
        }
        return formattedMessage;
    }
}