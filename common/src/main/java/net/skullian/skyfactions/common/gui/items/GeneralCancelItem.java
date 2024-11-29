package net.skullian.skyfactions.common.gui.items;

import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class GeneralCancelItem extends SkyItem {

    public GeneralCancelItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        player.closeInventory();
    }



}
