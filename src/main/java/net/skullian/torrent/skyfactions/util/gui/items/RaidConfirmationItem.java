package net.skullian.torrent.skyfactions.util.gui.items;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.island.IslandAPI;
import net.skullian.torrent.skyfactions.raid.RaidManager;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.sql.SQLException;
import java.util.List;

public class RaidConfirmationItem extends AbstractItem {

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.LIME_TERRACOTTA)
                .setDisplayName(Messages.RAID_CONFIRMATION_NAME.get());

        List<String> lore = SkyFactionsReborn.configHandler.MESSAGES_CONFIG.getStringList("Messages.Raiding.RAID_CONFIRMATION_LORE");
        for (String loreLine : lore) {
            itemBuilder.addLoreLines(TextUtility.color(loreLine.replace("%raid_cost%", SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Raiding.RAIDING_COST"))));
        }

        return itemBuilder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        event.getInventory().close();
        if (!RaidManager.hasEnoughGems(player)) return;
        RaidManager.startRaid(player);
    }
}
