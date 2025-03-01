package net.skullian.skyfactions.paper.hooks;

import net.milkbowl.vault.permission.Permission;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPIHook {

    private static Permission permissions;

    public static void init() {
        RegisteredServiceProvider<Permission> serviceProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (serviceProvider != null) {
            permissions = serviceProvider.getProvider();
        } else throw new NullPointerException("Failed to fetch Vault Permissions API!");
    }

    public static boolean isEnabled() {
        return permissions != null;
    }

    public static void addPermission(SkyUser player, String permission) {
        permissions.playerAdd(SpigotAdapter.adapt(player).getPlayer(), permission);
    }

    public static void removePermission(SkyUser player, String permission) {
        permissions.playerRemove(SpigotAdapter.adapt(player).getPlayer(), permission);
    }
}
