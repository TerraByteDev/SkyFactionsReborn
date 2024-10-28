package net.skullian.skyfactions.util;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.papi.PlaceholderManager;

import java.util.ArrayList;

import org.stringtemplate.v4.compiler.STParser.exprNoComma_return;


public class DependencyHandler {
    public static ArrayList<String> enabledDeps = new ArrayList<>();

    public static void init() {
        if (isPluginEnabled("PlaceholderAPI")) {
            SLogger.info("Found {} installed on the server - Registering expansion.", "\u001B[33mPlaceholderAPI\u001B[34m");
            new PlaceholderManager(SkyFactionsReborn.getInstance()).register();
            enabledDeps.add("PlaceholderAPI");
        } else alert("PlaceholderAPI");

        if (isPluginEnabled("JukeBox")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mJukeBox\u001B[34m");
            enabledDeps.add("JukeBox");
        } else alert("JukeBox");

        if (isPluginEnabled("NoteBlockAPI")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mNoteBlockAPI\u001B[34m");
            enabledDeps.add("NoteBlockAPI");
        } else alert("NoteBlockAPI");

        if (isPluginEnabled("MythicMobs")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mMythicMobs\u001B[34m");
            enabledDeps.add("MythicMobs");
        } else alert("MythicMobs");

        if (isPluginEnabled("Citizens")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mCitizens\u001B[34m");
            enabledDeps.add("Citizens");
        } else alert("Citizens");

        if (isPluginEnabled("ZNPCsPlus")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mZNPCsPlus\u001B[34m");
            enabledDeps.add("ZNPCsPlus");
        } else alert("ZNPCsPlus");

        if (isPluginEnabled("fancynpcs")) {
            SLogger.info("Found {} installed on the server.", "\001B[33mFancyNPCs\u001B[34m");
            enabledDeps.add("FancyNPCs");
        } else alert("FancyNPCs");
    }

    public static boolean isEnabled(String name) {
        return enabledDeps.contains(name);
    }

    private static boolean isPluginEnabled(String name) {
        return SkyFactionsReborn.getInstance().getServer().getPluginManager().isPluginEnabled(name);
    }

    public static void alert(String name) {
        SLogger.fatal("Could not find {} on the server! If you have this plugin installed, this is a bug!", "\u001B[33m" + name + "\u001B[31m");
    }
}
