package net.skullian.skyfactions.core.hooks;

import me.RockinChaos.itemjoin.api.ItemJoinAPI;
import net.skullian.skyfactions.core.util.DependencyHandler;
import org.bukkit.inventory.ItemStack;

public class ItemJoinHook {
    private static ItemJoinAPI itemJoinAPI;

    public static void init() {
        if (!DependencyHandler.enabledDeps.contains("ItemJoin")) {
            return;
        }

        itemJoinAPI = new ItemJoinAPI();
    }

    public static boolean isEnabled() {
        return itemJoinAPI != null;
    }

    public static boolean isCustom(ItemStack stack) {
        if (!isEnabled()) {
            return false;
        }

        return itemJoinAPI.isCustom(stack);
    }
}
