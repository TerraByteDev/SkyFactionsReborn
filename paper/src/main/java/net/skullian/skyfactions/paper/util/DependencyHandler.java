package net.skullian.skyfactions.paper.util;

import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.hooks.ItemJoinHook;
import net.skullian.skyfactions.paper.hooks.PlaceholderAPIHook;
import net.skullian.skyfactions.paper.hooks.VaultAPIHook;

import java.util.ArrayList;

public class DependencyHandler {

    public static ArrayList<String> enabledDeps = new ArrayList<>();

    public static void init() {
        if (isPluginEnabled("PlaceholderAPI")) {
            SLogger.info("Found {} installed on the server - Registering expansion.", "\u001B[33mPlaceholderAPI\u001B[34m");
            new PlaceholderAPIHook(SkyFactionsReborn.getInstance()).register();
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

        if (isPluginEnabled("ZNPCsPlus")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mZNPCsPlus\u001B[34m");
            enabledDeps.add("ZNPCsPlus");
        } else alert("ZNPCsPlus");

        if (isPluginEnabled("fancynpcs")) {
            SLogger.info("Found {} installed on the server.", "\001B[33mFancyNPCs\u001B[34m");
            enabledDeps.add("FancyNPCs");
        } else alert("FancyNPCs");

        if (isPluginEnabled("Vault")) {
            SLogger.info("Found {} installed on the server.", "\001B[33mVault\u001B[34m");
            VaultAPIHook.init();
            enabledDeps.add("Vault");
        } else alert("Vault");

        if (isPluginEnabled("ItemJoin")) {
            SLogger.info("Found {} installed on the server.", "\001B[33mItemJoin\u001B[34m");
            enabledDeps.add("ItemJoin");
            ItemJoinHook.init();
        } else alert("ItemJoin");

        if (isPluginEnabled("ItemsAdder")) {
            SLogger.info("Found {} installed on the server.", "\001B[33mItemsAdder\u001B[34m");
            enabledDeps.add("ItemsAdder");
        } else alert("ItemsAdder");

        if (isPluginEnabled("Oraxen")) {
            SLogger.info("Found {} installed on the server.", "\001B[33mOraxen\u001B[34m");
            enabledDeps.add("Oraxen");
        }
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
