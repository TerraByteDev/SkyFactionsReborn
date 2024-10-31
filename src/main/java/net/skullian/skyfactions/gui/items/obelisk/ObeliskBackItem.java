package net.skullian.skyfactions.gui.items.obelisk;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.FactionObeliskUI;
import net.skullian.skyfactions.gui.obelisk.PlayerObeliskUI;

public class ObeliskBackItem extends SkyItem {

    private String TYPE;

    public ObeliskBackItem(ItemData data, ItemStack stack, String type, Player player) {
        super(data, stack, player, null);
        
        this.TYPE = type;
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (player.hasMetadata("rune_ui")) {
            player.removeMetadata("rune_ui", SkyFactionsReborn.getInstance());
        }

        if (TYPE.equals("player")) {
            PlayerObeliskUI.promptPlayer(player);
        } else if (TYPE.equals("faction")) {
            FactionObeliskUI.promptPlayer(player);
        }
    }
}