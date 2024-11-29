package net.skullian.skyfactions.common.gui.items.obelisk;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class ObeliskFactionOverviewItem extends AsyncSkyItem {

    public ObeliskFactionOverviewItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }

    @Override
    public Object[] replacements() {

        Faction faction = SkyApi.getInstance().getFactionAPI().getFaction(getPLAYER().getUniqueId()).join();

        return List.of(
            "faction_name", faction.getName(),
            "level", faction.getLevel(),
            "member_count", faction.getTotalMemberCount(),
            "rune_count", faction.getRunes(),
            "gem_count", faction.getRunes()
        ).toArray();
    }


}
