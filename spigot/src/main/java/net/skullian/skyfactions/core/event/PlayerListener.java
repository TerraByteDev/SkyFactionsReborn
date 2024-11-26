package net.skullian.skyfactions.core.event;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.*;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.Settings;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.core.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.core.util.CooldownManager;
import net.skullian.skyfactions.core.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;


public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (SkyFactionsReborn.getDatabaseManager().closed) {
            event.getPlayer().kick(Component.text("<red>A fatal error occurred. Please contact your server owners to check logs."));
            throw new RuntimeException("Database is closed! Cannot allow player to join without risking dupes and unexpected functionalities.");
        }

        SpigotPlayerAPI.isPlayerRegistered(event.getPlayer().getUniqueId()).whenComplete((isRegistered, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            if (!isRegistered) {
                SLogger.info("Player [{}] has not joined before. Syncing with database.", event.getPlayer().getName());
                SkyFactionsReborn.getCacheService().getEntry(event.getPlayer().getUniqueId()).setShouldRegister(true);

                SpigotPlayerAPI.playerData.put(event.getPlayer().getUniqueId(), new PlayerData(
                        event.getPlayer().getUniqueId(),
                        "none",
                        0,
                        event.getPlayer().locale().getLanguage()
                ));
            } else {
                SkyFactionsReborn.getDatabaseManager().getPlayerManager().getPlayerLocale(event.getPlayer().getUniqueId()).whenComplete((locale, ex2) -> {
                    if (ex2 != null) {
                        ex2.printStackTrace();
                        return;
                    }

                    SpigotPlayerAPI.getPlayerData(event.getPlayer().getUniqueId());
                });
            }
        });

        SpigotPlayerAPI.cacheData(event.getPlayer());

        SpigotIslandAPI.getPlayerIsland(event.getPlayer().getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to get player {}'s Island - {}", event.getPlayer().getName(), ex.getMessage());
                ex.printStackTrace();
                return;
            }

            if (island != null) {
                DefencePlacementHandler.addPlacedDefences(event.getPlayer());
                if (Settings.ISLAND_TELEPORT_ON_JOIN.getBoolean()) {

                    World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                    if (world != null) {
                        Location centerLocation = island.getCenter(world);

                        SpigotRegionAPI.modifyWorldBorder(event.getPlayer(), island.getCenter(world), island.getSize());
                        SpigotRegionAPI.teleportPlayerToLocation(event.getPlayer(), centerLocation);

                    }
                }
            }
        });

        SLogger.info("Initialising Notification Task for {}", event.getPlayer().getName());
        SpigotNotificationAPI.createCycle(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent event) {
        Location hubLoc = SpigotRegionAPI.getHubLocation();
        SpigotFactionAPI.isInFaction(event.getPlayer()).whenComplete((is, ex) -> {
            if (ex != null) {
                event.setSpawnLocation(hubLoc);
                ex.printStackTrace();
            } else if (!is && event.getSpawnLocation().getWorld().getName().equals(Settings.ISLAND_FACTION_WORLD.getString())) {
                event.setSpawnLocation(hubLoc);
            }
        });

        SpigotIslandAPI.hasIsland(event.getPlayer().getUniqueId()).whenComplete((is, ex) -> {
            if (ex != null) {
                event.setSpawnLocation(hubLoc);
                ex.printStackTrace();
            } else if (!is && event.getSpawnLocation().getWorld().getName().equals(Settings.ISLAND_PLAYER_WORLD.getString())) {
                event.setSpawnLocation(hubLoc);
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        SLogger.info("Cancelling Notification Task for {}...", event.getPlayer().getName());
        BukkitTask task = SpigotNotificationAPI.tasks.get(event.getPlayer().getUniqueId());
        if (task != null) task.cancel();

        SpigotIslandAPI.modifyDefenceOperation(SpigotFactionAPI.DefenceOperation.DISABLE, event.getPlayer().getUniqueId());

        CooldownManager.ITEMS.remove(event.getPlayer());
        CooldownManager.COMMANDS.remove(event.getPlayer());
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        if (Settings.ISLAND_TELEPORT_ON_DEATH.getBoolean()) {
            if (SpigotRegionAPI.isLocationInRegion(event.getPlayer().getLocation(), "sfr_player_" + event.getPlayer().getUniqueId().toString()))
                SpigotIslandAPI.modifyDefenceOperation(SpigotFactionAPI.DefenceOperation.DISABLE, event.getPlayer().getUniqueId());
            SpigotIslandAPI.getPlayerIsland(event.getPlayer().getUniqueId()).whenComplete((island, ex) -> {
                if (ex != null) {
                    SLogger.fatal("Failed to get player {}'s Island - {}", event.getPlayer().getName(), ex.getMessage());
                    ex.printStackTrace();
                    return;
                }

                World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                if (island != null && world != null) {
                    event.getPlayer().teleport(island.getCenter(world));
                }
            });
        }
    }

    @EventHandler
    public void onPlayerDimensionChange(PlayerPortalEvent event) {
        if (Settings.ISLAND_PREVENT_NETHER_PORTALS.getBoolean()) {
            List<String> allowedDims = Settings.ISLAND_ALLOWED_DIMENSIONS.getList();
            if (!allowedDims.contains(event.getFrom().getWorld().getName())) {
                Messages.NETHER_PORTALS_BLOCKED.send(event.getPlayer(), event.getPlayer().locale().getLanguage());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().hasMetadata("inFactionRelatedUI")) {
            event.getPlayer().removeMetadata("inFactionRelatedUI", SkyFactionsReborn.getInstance());
        }
    }
}
