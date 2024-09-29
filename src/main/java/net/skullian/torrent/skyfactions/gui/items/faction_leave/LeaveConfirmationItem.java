package net.skullian.torrent.skyfactions.gui.items.faction_leave;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.api.IslandAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.island.PlayerIsland;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class LeaveConfirmationItem extends AbstractItem {

    private String NAME;
    private String SOUND;
    private int PITCH;
    private List<String> LORE;
    private ItemStack STACK;

    public LeaveConfirmationItem(ItemData data, ItemStack stack) {
        this.NAME = data.getNAME();
        this.SOUND = data.getSOUND();
        this.PITCH = data.getPITCH();
        this.LORE = data.getLORE();
        this.STACK = stack;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.color(NAME));

        for (String loreLine : LORE) {
            builder.addLoreLines(TextUtility.color(loreLine));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        event.getInventory().close();

        if (!SOUND.equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, SOUND, PITCH, 1);
        }

        // We do this again in case they get kicked before the confirmation.
        Faction faction = FactionAPI.getFaction(player);
        if (faction != null) {
            World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
            if (world != null) {
                if (FactionAPI.isInRegion(player, world, faction.getName())) {

                    PlayerIsland island = SkyFactionsReborn.db.getPlayerIsland(player.getUniqueId()).join();
                    World islandWorld = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                    if (island != null && islandWorld != null) {
                        IslandAPI.teleportPlayerToLocation(player, island.getCenter(islandWorld));
                    } else {
                        World hubWorld = Bukkit.getWorld(Settings.HUB_WORLD_NAME.getString());
                        if (hubWorld != null) {
                            List<Integer> hubLocArray = Settings.HUB_LOCATION.getIntegerList();
                            Location location = new Location(hubWorld, hubLocArray.get(0), hubLocArray.get(1), hubLocArray.get(2));
                            IslandAPI.teleportPlayerToLocation(player, location);
                        } else {
                            Messages.ERROR.send(player, "%operation%", "leave the faction", "%debug%", "WORLD_NOT_EXIST");
                            return;
                        }
                    }

                }

                faction.leaveFaction(Bukkit.getOfflinePlayer(player.getUniqueId()));
                Messages.FACTION_LEAVE_SUCCESS.send(player, "%faction_name%", faction.getName());
            } else {
                Messages.ERROR.send(player, "%operation%", "leave the faction", "%debug%", "WORLD_NOT_EXIST");

            }
        } else {
            Messages.NOT_IN_FACTION.send(player);
        }

    }

}
