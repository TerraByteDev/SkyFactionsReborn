package net.skullian.torrent.skyfactions.util;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class SLogger {

    public static final ComponentLogger LOGGER = ComponentLogger.logger("");

    public static void info(String format, Object... args) {
        LOGGER.info("\u001B[32m[SkyFactionsReborn]\u001B[34m " + format + "\u001B[0m", args);
    }

    public static void warn(String format, Object... args) {
        LOGGER.warn("\u001B[33m[\u001B[32mSkyFactionsReborn\u001B[33m] " + format + "\u001B[0m", args);
    }

    public static void fatal(String format, Object... args) {
        LOGGER.warn("\u001B[31m[SkyFactionsReborn] " + format + "\u001B[0m", args);
    }
}