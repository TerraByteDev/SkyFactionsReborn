package net.skullian.torrent.skyfactions.util.gui.items;


import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.island.IslandAPI;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.ArrayList;
import java.util.List;

public class CreationConfirmationItem extends AbstractItem {

    @Override
    public ItemProvider getItemProvider() {

        return new ItemBuilder(Material.LIME_TERRACOTTA)
                .setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a&lConfirm"));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        IslandAPI.createIsland((Player) event.getWhoClicked());
        event.getInventory().close();

    }

}
