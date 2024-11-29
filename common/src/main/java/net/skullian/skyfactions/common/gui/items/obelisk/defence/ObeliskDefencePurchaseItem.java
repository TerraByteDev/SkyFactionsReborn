package net.skullian.skyfactions.common.gui.items.obelisk.defence;

import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.defence.ObeliskDefencePurchaseOverviewUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class ObeliskDefencePurchaseItem extends SkyItem {

    private String TYPE;
    private Faction FACTION;

    public ObeliskDefencePurchaseItem(ItemData data, SkyItemStack stack, String type, Faction faction, SkyUser player) {
        super(data, stack, player, null);
        
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (TYPE.equals("faction")) {
            ObeliskDefencePurchaseOverviewUI.promptPlayer(player, TYPE, FACTION);
        } else if (TYPE.equals("player")) ObeliskDefencePurchaseOverviewUI.promptPlayer(player, TYPE, null);

    }
}
