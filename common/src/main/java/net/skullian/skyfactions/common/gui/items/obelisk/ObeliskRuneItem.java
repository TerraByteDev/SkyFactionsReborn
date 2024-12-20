package net.skullian.skyfactions.common.gui.items.obelisk;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.RunesSubmitUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class ObeliskRuneItem extends AsyncSkyItem {

    private String TYPE;

    public ObeliskRuneItem(ItemData data, SkyItemStack stack, String type, SkyUser player) {
        super(data, stack, player, List.of(type).toArray());

        this.TYPE = type;
    }

    @Override
    public Object[] replacements() {
        String type = (String) getOPTIONALS()[0];

        int runes = 0;
        if (type.equals("player")) {
            runes = getPLAYER().getRunes().join();
        } else if (type.equals("faction")) {
            Faction faction = SkyApi.getInstance().getFactionAPI().getFaction(getPLAYER().getUniqueId()).join();
            if (faction != null) {
                runes = faction.getRunes();
            }
        }

        return List.of("runes", runes).toArray();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        RunesSubmitUI.promptPlayer(player, TYPE);
    }

}
