package net.skullian.skyfactions.common.gui.items.rune_submit;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.Arrays;
import java.util.List;

public class RuneSubmitItem extends SkyItem {

    private List<SkyItemStack> INVENTORY;
    private String TYPE;

    public RuneSubmitItem(ItemData data, SkyItemStack stack, String type, List<SkyItemStack> inventory, SkyUser player) {
        super(data, stack, player, null);
        this.INVENTORY = inventory;
        this.TYPE = type;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        player.closeInventory();
        player.removeMetadata("rune_ui");
        if (TYPE.equals("player")) {
            SkyApi.getInstance().getRunesAPI().handleRuneConversion(INVENTORY, player);
        } else if (TYPE.equals("faction")) {
            SkyApi.getInstance().getRunesAPI().handleFactionRuneConversion(INVENTORY, player);
        }

        INVENTORY.clear();
    }
}
