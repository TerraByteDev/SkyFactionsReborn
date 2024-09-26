package net.skullian.torrent.skyfactions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLogger {

    public static final Logger LOGGER = LoggerFactory.getLogger("\u001B[32mSkyFactionsReborn\u001B[0m");

    public static void info(Object... args) {
        LOGGER.info("\u001B[34m" + build(args) + "\u001B[0m");
    }

    public static void fatal(Object... args) {
        LOGGER.error(build(args));
    }

    public static void warn(Object... args) {
        LOGGER.warn(build(args));
    }

    private static String build(Object... args) {
        StringBuilder messageBuilder = new StringBuilder();
        for (Object arg : args) {
            messageBuilder.append(arg.toString()).append(" ");
        }
        return messageBuilder.toString().trim();
    }
}