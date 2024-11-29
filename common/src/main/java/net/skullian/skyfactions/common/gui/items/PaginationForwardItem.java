package net.skullian.skyfactions.common.gui.items;

import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.impl.SkyPageItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class PaginationForwardItem extends SkyPageItem {


    public PaginationForwardItem(ItemData data, SkyItemStack stack, SkyUser player, Object[] optionals, PaginationItemData paginationItemData) {
        super(data, stack, player, optionals, paginationItemData, true);
    }

    @Override
    public int getCurrentPage() {
        return 0;
    }

    @Override
    public int getPageAmount() {
        return 0;
    }
}
