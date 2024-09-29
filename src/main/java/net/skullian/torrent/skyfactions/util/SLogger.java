package net.skullian.torrent.skyfactions.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLogger {

    public static final Logger LOGGER = LoggerFactory.getLogger("\u001B[32mSkyFactionsReborn\u001B[0m");

    public static void info(String format, Object... args) {
        LOGGER.info("\u001B[34m" + format + "\u001B[0m", args);
    }

    public static void fatal(String format, Object... args) {
        LOGGER.error(format, args);
    }

    public static void warn(String format, Object... args) {
        LOGGER.warn(format, args);
    }
}