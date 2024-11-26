package net.skullian.skyfactions.core.gui.items.obelisk;

import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.core.api.SpigotRunesAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.AsyncSkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.RunesSubmitUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObeliskRuneItem extends AsyncSkyItem {

    private String TYPE;

    public ObeliskRuneItem(ItemData data, ItemStack stack, String type, Player player) {
        super(data, stack, player, List.of(type).toArray());

        this.TYPE = type;
    }

    @Override
    public Object[] replacements() {
        String type = (String) getOptionals()[0];

        int runes = 0;
        if (type.equals("player")) {
            runes = SpigotRunesAPI.getRunes(getPLAYER().getUniqueId()).join();
        } else if (type.equals("faction")) {
            Faction faction = SpigotFactionAPI.getFaction(getPLAYER().getUniqueId()).join();
            if (faction != null) {
                runes = faction.getRunes();
            }
        }

        return List.of("runes", runes).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        RunesSubmitUI.promptPlayer(player, TYPE);
    }

}
