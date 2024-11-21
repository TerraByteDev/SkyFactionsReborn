package net.skullian.skyfactions.gui.items.faction_leave;

import java.util.List;

import net.skullian.skyfactions.api.RegionAPI;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.faction.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.ErrorUtil;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class LeaveConfirmationItem extends SkyItem {

    public LeaveConfirmationItem(ItemData data, ItemStack stack, Player player) {
        super(data, stack, player, null);
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) {
        Faction faction = FactionAPI.getCachedFaction(getPLAYER().getUniqueId());

        if (faction != null && faction.isOwner(getPLAYER())) {
            builder.addLoreLines(toList(Messages.FACTION_LEAVE_OWNER_CONFIRMATION_LORE.getStringList(PlayerHandler.getLocale(getPLAYER().getUniqueId()))));
        }

        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.getInventory().close();
        // We do this again in case they get kicked before the confirmation.
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
                if (world != null) {
                    if (FactionAPI.isInRegion(player, faction.getName())) {

                        IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, exc) -> {
                            if (exc != null) {
                                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", exc);
                                return;
                            }

                            World islandWorld = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                            if (island != null && islandWorld != null) {
                                RegionAPI.teleportPlayerToLocation(player, island.getCenter(islandWorld));
                            } else {
                                World hubWorld = Bukkit.getWorld(Settings.HUB_WORLD_NAME.getString());
                                if (hubWorld != null) {
                                    List<Integer> hubLocArray = Settings.HUB_LOCATION.getIntegerList();
                                    Location location = new Location(hubWorld, hubLocArray.get(0), hubLocArray.get(1), hubLocArray.get(2));
                                    RegionAPI.teleportPlayerToLocation(player, location);
                                } else {
                                    Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "leave the faction", "debug", "WORLD_NOT_EXIST");
                                }
                            }
                        });

                    }

                    faction.leaveFaction(Bukkit.getOfflinePlayer(player.getUniqueId()));
                    Messages.FACTION_LEAVE_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "faction_name", faction.getName());
                } else {
                    Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "leave the faction", "debug", "WORLD_NOT_EXIST");

                }
            } else {
                Messages.NOT_IN_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            }
        });

    }

}
