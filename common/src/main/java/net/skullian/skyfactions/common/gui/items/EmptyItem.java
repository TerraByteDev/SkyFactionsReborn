package net.skullian.skyfactions.common.gui.items;

import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class EmptyItem extends SkyItem {

    public EmptyItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }
}