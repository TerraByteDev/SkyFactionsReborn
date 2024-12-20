package net.skullian.skyfactions.common.gui.items.obelisk;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class ObeliskHeadItem extends AsyncSkyItem {

    public ObeliskHeadItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }

    @Override
    public Object[] replacements() {
        
        PlayerIsland island = SkyApi.getInstance().getIslandAPI().getPlayerIsland(getPLAYER().getUniqueId()).join();

        return List.of(
            "player_name", getPLAYER().getName(),
            "level", island == null ? "N/A" : SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().getIslandLevel(island).join(),
            "rune_count", getPLAYER().getRunes().join(),
            "gem_count", getPLAYER().getGems().join()
        ).toArray();
    }
}
