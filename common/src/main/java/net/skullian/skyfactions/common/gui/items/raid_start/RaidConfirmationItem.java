package net.skullian.skyfactions.common.gui.items.raid_start;

import net.skullian.skyfactions.core.api.SpigotRaidAPI;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RaidConfirmationItem extends SkyItem {

    public RaidConfirmationItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();

        if (!SpigotRaidAPI.hasEnoughGems(player)) return;
        SpigotRaidAPI.startRaid(player);
    }
}
