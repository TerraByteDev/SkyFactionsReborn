package net.skullian.skyfactions.gui.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;

public class EmptyItem extends SkyItem {

    public EmptyItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }
}