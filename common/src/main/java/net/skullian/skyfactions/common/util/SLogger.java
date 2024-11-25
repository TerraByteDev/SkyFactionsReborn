package net.skullian.skyfactions.common.util;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class SLogger {

    public static final ComponentLogger LOGGER = ComponentLogger.logger("SkyFactionsReborn");

    public static void info(Object message, Object... args) {
        LOGGER.info("\u001B[34m" + message + "\u001B[0m", args);
    }

    public static void warn(Object message, Object... args) {
        LOGGER.warn(message + "\u001B[0m", args);
    }

    public static void fatal(Object message, Object... args) {
        LOGGER.error(message + "\u001B[0m", args);
    }

}