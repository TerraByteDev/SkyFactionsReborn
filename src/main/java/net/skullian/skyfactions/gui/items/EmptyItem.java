package net.skullian.skyfactions.gui.items;

import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class EmptyItem extends SkyItem {

    public EmptyItem(ItemData data, ItemStack stack) {
        super(data, stack, null);
    }
}