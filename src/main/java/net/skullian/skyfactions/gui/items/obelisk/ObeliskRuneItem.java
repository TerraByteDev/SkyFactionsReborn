package net.skullian.skyfactions.gui.items.obelisk;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.gui.obelisk.RunesSubmitUI;

public class ObeliskRuneItem extends AsyncSkyItem {

    private String TYPE;

    public ObeliskRuneItem(ItemData data, ItemStack stack, String type, Player player) {
        super(data, stack, player, List.of(type).toArray());

        this.TYPE = type;
    }

    public Object[] replacements() {
        String type = (String) getOptionals()[0];

        int runes = 0;
        if (type.equals("player")) {
            runes = RunesAPI.getRunes(getPLAYER().getUniqueId());
        } else if (type.equals("faction")) {
            Faction faction = FactionAPI.getFaction(getPLAYER().getUniqueId()).join();
            if (faction != null) {
                runes = faction.getRunes();
            }
        }

        return List.of("%runes%", runes).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        new RunesSubmitUI().promptPlayer(player, TYPE);
    }

}
