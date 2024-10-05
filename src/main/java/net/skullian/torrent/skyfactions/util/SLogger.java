package net.skullian.torrent.skyfactions.util;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;

public class SLogger {

    public static final ComponentLogger LOGGER = ComponentLogger.logger("SkyFactionsReborn");

    public static void info(Object message, Object... args) {
        synchronized (SkyFactionsReborn.class) {
            LOGGER.info("\u001B[34m" + message + "\u001B[0m", args);
        }
    }

    public static void warn(Object message, Object... args) {
        synchronized (SkyFactionsReborn.class) {
            LOGGER.warn(message + "\u001B[0m", args);
        }
    }

    public static void fatal(Object message, Object... args) {
        synchronized (SkyFactionsReborn.class) {
            LOGGER.error(message + "\u001B[0m", args);
        }
    }
}