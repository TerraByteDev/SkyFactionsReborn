package net.skullian.torrent.skyfactions.util;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.papi.PlaceholderManager;


public class DependencyHandler {

    public static boolean jukebox = false;
    public static boolean papi = false;
    public static boolean nbapi = false;

    public static void init() {
        if (isPluginEnabled("PlaceholderAPI")) {
            SLogger.info("Syncing with PAPI.");
            new PlaceholderManager(SkyFactionsReborn.getInstance()).register();
            papi = true;
        } else {
            alert("PlaceholderAPI");
        }

        if (isPluginEnabled("JukeBox")) {
            SLogger.info("Found JukeBox installed on the server.");
            jukebox = true;
        }

        if (isPluginEnabled("NoteBlockAPI")) {
            SLogger.info("Found NoteBlockAPI installed on the server.");
            nbapi = true;
        }
    }

    private static boolean isPluginEnabled(String name) {
        return SkyFactionsReborn.getInstance().getServer().getPluginManager().isPluginEnabled(name);
    }

    private static void alert(String name) {
        SLogger.warn("Could not find {} on the server!", name);
        SLogger.warn("If you have {} installed, this is a bug!", name);
    }
}
