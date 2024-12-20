package net.skullian.skyfactions.common.gui.items.obelisk;

import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.FactionObeliskUI;
import net.skullian.skyfactions.common.gui.screens.obelisk.PlayerObeliskUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class ObeliskBackItem extends SkyItem {

    private String TYPE;

    public ObeliskBackItem(ItemData data, SkyItemStack stack, String type, SkyUser player) {
        super(data, stack, player, null);
        
        this.TYPE = type;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (player.hasMetadata("rune_ui")) {
            player.removeMetadata("rune_ui");
        }

        if (TYPE.equals("player")) {
            PlayerObeliskUI.promptPlayer(player);
        } else if (TYPE.equals("faction")) {
            FactionObeliskUI.promptPlayer(player);
        }
    }
}