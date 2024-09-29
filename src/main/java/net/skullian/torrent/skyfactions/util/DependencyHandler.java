package net.skullian.torrent.skyfactions.util;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.papi.PlaceholderManager;


public class DependencyHandler {

    public static boolean jukebox = false;
    public static boolean papi = false;
    public static boolean nbapi = false;

    public static void init() {
        if (isPluginEnabled("PlaceholderAPI")) {
            SLogger.info("Found {} installed on the server - Registering expansion.", "\u001B[33mPlaceholderAPI\u001B[34m");
            new PlaceholderManager(SkyFactionsReborn.getInstance()).register();
            papi = true;
        } else alert("PlaceholderAPI");

        if (isPluginEnabled("JukeBox")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mJukeBox\u001B[34m");
            jukebox = true;
        } else alert("JukeBox");

        if (isPluginEnabled("NoteBlockAPI")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mNoteBlockAPI\u001B[34m");
            nbapi = true;
        } else alert("NoteBlockAPI");
    }

    private static boolean isPluginEnabled(String name) {
        return SkyFactionsReborn.getInstance().getServer().getPluginManager().isPluginEnabled(name);
    }

    private static void alert(String name) {
        SLogger.fatal("Could not find {} on the server!", "\u001B[33m" + name + "\u001B[31m");
        SLogger.fatal("If you have this plugin installed, this is a bug!");
    }
}
