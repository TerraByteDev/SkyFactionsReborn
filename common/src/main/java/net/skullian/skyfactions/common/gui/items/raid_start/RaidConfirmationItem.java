package net.skullian.skyfactions.common.gui.items.raid_start;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class RaidConfirmationItem extends SkyItem {

    public RaidConfirmationItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        player.closeInventory();

        if (!SkyApi.getInstance().getRaidAPI().hasEnoughGems(player)) return;
        SkyApi.getInstance().getRaidAPI().startRaid(player);
    }
}
