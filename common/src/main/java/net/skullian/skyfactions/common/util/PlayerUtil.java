package net.skullian.skyfactions.common.util;

import net.skullian.skyfactions.core.hooks.ItemJoinHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtil {
    public static void clearInventory(Player player) {
        ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];

            if (item == null || item.getType().isAir()) {
                continue;
            }

            if (ItemJoinHook.isEnabled() && ItemJoinHook.isCustom(item)) {
                continue;
            }

            player.getInventory().clear(i);
        }
    }

    public static void clearEnderChest(Player player) {
        player.getEnderChest().clear();
    }
}
