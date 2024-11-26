package net.skullian.skyfactions.core.hooks;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPIHook {

    private static Permission permissions;

    public static void init() {
        RegisteredServiceProvider<Permission> serviceProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permissions = serviceProvider.getProvider();
    }

    public static boolean isEnabled() {
        return permissions != null;
    }

    public static void addPermission(Player player, String permission) {
        permissions.playerAdd(player, permission);
    }

    public static void removePermission(Player player, String permission) {
        permissions.playerRemove(player, permission);
    }
}
