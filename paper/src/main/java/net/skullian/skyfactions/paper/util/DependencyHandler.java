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
            SLogger.setup("Found {} installed on the server - Registering expansion.", false, "<#05eb2f>PlaceholderAPI<#4294ed>");
            new PlaceholderAPIHook(SkyFactionsReborn.getInstance()).register();
            enabledDeps.add("PlaceholderAPI");
        } else alert("PlaceholderAPI");

        if (isPluginEnabled("JukeBox")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>JukeBox<#4294ed>");
            enabledDeps.add("JukeBox");
        } else alert("JukeBox");

        if (isPluginEnabled("NoteBlockAPI")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>NoteBlockAPI<#4294ed>");
            enabledDeps.add("NoteBlockAPI");
        } else alert("NoteBlockAPI");

        if (isPluginEnabled("MythicMobs")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>MythicMobs<#4294ed>");
            enabledDeps.add("MythicMobs");
        } else alert("MythicMobs");

        if (isPluginEnabled("ZNPCsPlus")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>ZNPCsPlus<#4294ed>");
            enabledDeps.add("ZNPCsPlus");
        } else alert("ZNPCsPlus");

        if (isPluginEnabled("fancynpcs")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>FancyNPCs<#4294ed>");
            enabledDeps.add("FancyNPCs");
        } else alert("FancyNPCs");

        if (isPluginEnabled("Vault")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>Vault<#4294ed>");
            VaultAPIHook.init();
            enabledDeps.add("Vault");
        } else alert("Vault");

        if (isPluginEnabled("ItemJoin")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>ItemJoin<#4294ed>");
            enabledDeps.add("ItemJoin");
            ItemJoinHook.init();
        } else alert("ItemJoin");

        if (isPluginEnabled("ItemsAdder")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>ItemsAdder<#4294ed>");
            enabledDeps.add("ItemsAdder");
        } else alert("ItemsAdder");

        if (isPluginEnabled("Oraxen")) {
            SLogger.setup("Found {} installed on the server.", false, "<#05eb2f>Oraxen<#4294ed>");
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
        SLogger.setup("<red>Could not find {} on the server!", false, name );
    }
}
