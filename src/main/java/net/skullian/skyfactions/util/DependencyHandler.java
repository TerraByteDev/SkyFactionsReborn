package net.skullian.skyfactions.util;

import net.milkbowl.vault.permission.Permission;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.hooks.ItemJoinHook;
import net.skullian.skyfactions.hooks.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;

public class DependencyHandler {
    public static Permission vaultPermissions;

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

        if (isPluginEnabled("ZNPCsPlus")) {
            SLogger.info("Found {} installed on the server.", "\u001B[33mZNPCsPlus\u001B[34m");
            enabledDeps.add("ZNPCsPlus");
        } else alert("ZNPCsPlus");

        if (isPluginEnabled("fancynpcs")) {
            SLogger.info("Found {} installed on the server.", "\001B[33mFancyNPCs\u001B[34m");
            enabledDeps.add("FancyNPCs");
        } else alert("FancyNPCs");

        if (isPluginEnabled("Vault")) {
            RegisteredServiceProvider<Permission> serviceProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            vaultPermissions = serviceProvider.getProvider();

            enabledDeps.add("Vault");
            SLogger.info("Found {} installed on the server.", "\001B[33mVault\u001B[34m");
        } else alert("Vault");

        if (isPluginEnabled("ItemJoin")) {
            enabledDeps.add("ItemJoin");
            ItemJoinHook.init();
            SLogger.info("Found {} installed on the server.", "\001B[33mItemJoin\u001B[34m");
        } else alert("ItemJoin");
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
