package net.skullian.skyfactions.common.gui.items;

import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.impl.SkyPageItem;
import net.skullian.skyfactions.common.user.SkyUser;

public class PaginationForwardItem extends SkyPageItem {


    public PaginationForwardItem(PaginationItemData paginationItemData, SkyUser player) {
        super(null, null, player, null, paginationItemData, true);
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
