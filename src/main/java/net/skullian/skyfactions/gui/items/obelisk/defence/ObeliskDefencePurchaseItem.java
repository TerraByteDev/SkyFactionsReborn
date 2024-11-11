package net.skullian.skyfactions.gui.items.obelisk.defence;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.screens.obelisk.defence.ObeliskDefencePurchaseOverviewUI;

public class ObeliskDefencePurchaseItem extends SkyItem {

    private String TYPE;
    private Faction FACTION;

    public ObeliskDefencePurchaseItem(ItemData data, ItemStack stack, String type, Faction faction, Player player) {
        super(data, stack, player, null);
        
        this.TYPE = type;
        this.FACTION = faction;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (TYPE.equals("faction")) {
            if (FACTION.isOwner(player) || FACTION.isModerator(player) || FACTION.isAdmin(player)) {
                ObeliskDefencePurchaseOverviewUI.promptPlayer(player, TYPE, FACTION);
            }
        } else if (TYPE.equals("player")) ObeliskDefencePurchaseOverviewUI.promptPlayer(player, TYPE, null);

    }
}
