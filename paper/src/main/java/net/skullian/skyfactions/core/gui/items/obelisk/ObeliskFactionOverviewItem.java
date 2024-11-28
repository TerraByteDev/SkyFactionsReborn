package net.skullian.skyfactions.core.gui.items.obelisk;

import java.util.List;

import net.skullian.skyfactions.common.gui.data.ItemData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.items.impl.old.AsyncSkyItem;

public class ObeliskFactionOverviewItem extends AsyncSkyItem {

    public ObeliskFactionOverviewItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public Object[] replacements() {

        Faction faction = SpigotFactionAPI.getFaction(getPLAYER().getUniqueId()).join();

        return List.of(
            "faction_name", faction.getName(),
            "level", faction.getLevel(),
            "member_count", faction.getTotalMemberCount(),
            "rune_count", faction.getRunes(),
            "gem_count", faction.getRunes()
        ).toArray();
    }


}
