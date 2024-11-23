package net.skullian.skyfactions.gui.items.raid_start;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.RaidAPI;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;

public class RaidConfirmationItem extends SkyItem {

    public RaidConfirmationItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        if (!RaidAPI.hasEnoughGems(player)) return;
        RaidAPI.startRaid(player);
    }
}
