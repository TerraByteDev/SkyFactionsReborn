package net.skullian.skyfactions.gui.items.obelisk;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.AsyncSkyItem;

public class ObeliskFactionOverviewItem extends AsyncSkyItem {

    public ObeliskFactionOverviewItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public Object[] replacements() {

        Faction faction = FactionAPI.getFaction(getPLAYER().getUniqueId()).join();

        return List.of(
            "%faction_name%", faction.getName(),
            "%level%", faction.getLevel(),
            "%member_count%", faction.getTotalMemberCount(),
            "%rune_count%", faction.getRunes(),
            "%gem_count%", faction.getRunes()
        ).toArray();
    }


}
