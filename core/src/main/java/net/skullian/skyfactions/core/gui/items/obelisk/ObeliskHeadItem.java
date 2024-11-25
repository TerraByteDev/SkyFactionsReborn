package net.skullian.skyfactions.core.gui.items.obelisk;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.GemsAPI;
import net.skullian.skyfactions.core.api.SpigotIslandAPI;
import net.skullian.skyfactions.core.api.SpigotRunesAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.core.island.impl.PlayerIsland;

public class ObeliskHeadItem extends AsyncSkyItem {

    public ObeliskHeadItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public Object[] replacements() {
        
        PlayerIsland island = SpigotIslandAPI.getPlayerIsland(getPLAYER().getUniqueId()).join();

        return List.of(
            "player_name", getPLAYER().getName(),
            "level", island == null ? "N/A" : SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().getIslandLevel(island).join(),
            "rune_count", SpigotRunesAPI.getRunes(getPLAYER().getUniqueId()).join(),
            "gem_count", GemsAPI.getGems(getPLAYER().getUniqueId()).join()
        ).toArray();
    }
}
