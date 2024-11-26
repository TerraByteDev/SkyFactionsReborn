package net.skullian.skyfactions.core.gui.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.SkyItem;

public class EmptyItem extends SkyItem {

    public EmptyItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }
}