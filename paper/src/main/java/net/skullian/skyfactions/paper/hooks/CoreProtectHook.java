package net.skullian.skyfactions.paper.hooks;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.skullian.skyfactions.common.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CoreProtectHook {

    private static CoreProtectAPI coreProtectAPI;

    public static void init() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
        if (!(plugin instanceof CoreProtect pluginInstance)) {
            SLogger.setup("Failed to fetch CoreProtect Instance!", true);
            return;
        }

        if (!pluginInstance.getAPI().isEnabled()) {
            SLogger.setup("CoreProtect's API is not enabled!", true);
        } else {
            coreProtectAPI = pluginInstance.getAPI();
        }
    }

    public static boolean isEnabled() {
        return coreProtectAPI != null && coreProtectAPI.isEnabled();
    }

    public static void onDefenceBreak() {

    }
}
