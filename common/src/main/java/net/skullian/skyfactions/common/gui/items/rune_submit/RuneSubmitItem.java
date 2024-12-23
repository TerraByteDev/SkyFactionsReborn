package net.skullian.skyfactions.common.gui.items.rune_submit;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.RunesSubmitUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class RuneSubmitItem extends SkyItem {

    private final RunesSubmitUI INVENTORY;
    private final String TYPE;

    public RuneSubmitItem(ItemData data, SkyItemStack stack, String type, RunesSubmitUI inventory, SkyUser player) {
        super(data, stack, player, null);
        this.INVENTORY = inventory;
        this.TYPE = type;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        player.closeInventory();
        player.removeMetadata("rune_ui");
        if (TYPE.equals("player")) {
            SkyApi.getInstance().getRunesAPI().handlePlayerRuneConversion(INVENTORY.getInventory().values().stream().toList(), player);
        } else if (TYPE.equals("faction")) {
            SkyApi.getInstance().getRunesAPI().handleFactionRuneConversion(INVENTORY.getInventory().values().stream().toList(), player);
        }

        INVENTORY.getInventory().clear();
    }
}
