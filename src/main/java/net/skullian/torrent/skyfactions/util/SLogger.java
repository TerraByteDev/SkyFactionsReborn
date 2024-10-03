package net.skullian.torrent.skyfactions.util;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;

public class SLogger {

    public static final ComponentLogger LOGGER = ComponentLogger.logger("");

    public static void info(Object message, Object... args) {
        synchronized (SkyFactionsReborn.class) {
            LOGGER.info("\u001B[32m[SkyFactionsReborn]\u001B[34m " + message + "\u001B[0m", args);
        }
    }

    public static void warn(Object message, Object... args) {
        synchronized (SkyFactionsReborn.class) {
            LOGGER.warn("\u001B[33m[\u001B[32mSkyFactionsReborn\u001B[33m] " + message + "\u001B[0m", args);
        }
    }

    public static void fatal(Object message, Object... args) {
        synchronized (SkyFactionsReborn.class) {
            LOGGER.error("\u001B[31m[SkyFactionsReborn] " + message + "\u001B[0m", args);
        }
    }
}