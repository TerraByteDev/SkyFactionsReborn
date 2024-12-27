package net.skullian.skyfactions.common.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.skullian.skyfactions.common.api.SkyApi;

public class SLogger {

    private static final String PREFIX = "<gray>[<reset><gradient:#0083FF:#00FFC7><bold>SkyFactions</gradient><reset><gray>]<reset>";

    public static void setup(Object message, boolean fatal, Object... args) {
        String text = fatal ? "<#e73f38>✗ㅤㅤ" + format(message, args) + "<#4294ed>" : "<#4294ed>➤ㅤㅤ" + format(message, args);
        String formatted = getFormatted(text);

        SkyApi.getInstance().getConsoleAudience().sendMessage(MiniMessage.miniMessage().deserialize("<bold>" + formatted + "<reset>"));
    }

    public static String getFormatted(String text) {
        String without = PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(text));

        int totalLength = 62;
        String leftDelimiter = "│";
        String rightDelimiter = "│";
        int availableSpace = totalLength - (leftDelimiter.length() + rightDelimiter.length());

        if (without.length() > availableSpace) {
            without = without.substring(0, availableSpace);
        }

        int paddingLeft = (availableSpace - without.length()) / 2;
        int paddingRight = availableSpace - without.length() - paddingLeft;
        return leftDelimiter + getPadding(paddingLeft) + text + getPadding(paddingRight) + rightDelimiter;
    }

    public static void noPrefix(Object message, Object... args) {
        String formatted = format(message, args);
        SkyApi.getInstance().getConsoleAudience().sendMessage(MiniMessage.miniMessage().deserialize("<#4294ed>"+ "<bold>" + formatted + "<reset>"));
    }

    private static String getPadding(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static void info(Object message, Object... args) {
        Component infoLog = MiniMessage.miniMessage().deserialize(PREFIX + " <#4294ed>" + format(message, args) + "<#4294ed><reset>");
        SkyApi.getInstance().getConsoleAudience().sendMessage(infoLog);
    }

    public static void warn(Object message, Object... args) {
        Component warnLog = MiniMessage.miniMessage().deserialize(PREFIX + " <#f28f24>" + format(message, args) + "<#f28f24><reset>");
        SkyApi.getInstance().getConsoleAudience().sendMessage(warnLog);
    }

    public static void fatal(Object message, Object... args) {
        Component fatalLog = MiniMessage.miniMessage().deserialize(PREFIX + " <#e73f38>" + format(message, args) + "<#e73f38><reset>");
        SkyApi.getInstance().getConsoleAudience().sendMessage(fatalLog);
    }

    private static String format(Object message, Object... args) {
        if (message == null) {
            return null;
        }
        String formattedMessage = message.toString();
        for (Object arg : args) {
            if (arg == null) continue;
            formattedMessage = formattedMessage.replaceFirst("\\{}", arg.toString());
        }
        return formattedMessage;
    }
}