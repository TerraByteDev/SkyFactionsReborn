package net.skullian.skyfactions.paper.event;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.gui.CooldownManager;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.paper.api.SpigotNotificationAPI;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.event.defence.DefencePlacementHandler;
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
        if (SkyApi.getInstance().getDatabaseManager().closed) {
            event.getPlayer().kick(Component.text("<red>A fatal error occurred. Please contact your server owners to check logs."));
            throw new IllegalStateException("Database is closed! Cannot allow player to join without risking dupes and unexpected functionalities.");
        }

        SkyUser user = SkyApi.getInstance().getUserManager().getUser(event.getPlayer().getUniqueId());
        SkyApi.getInstance().getPlayerAPI().isPlayerRegistered(event.getPlayer().getUniqueId()).whenComplete((isRegistered, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to get registration status for player {} - {}", event.getPlayer().getName(), ex);
                return;
            }

            if (!isRegistered) {
                SLogger.info("Player [{}] has not joined before. Syncing with database.", event.getPlayer().getName());
                SkyApi.getInstance().getCacheService().getEntry(event.getPlayer().getUniqueId()).setShouldRegister(true);

                SkyApi.getInstance().getPlayerAPI().getPlayerData().put(event.getPlayer().getUniqueId(), new PlayerData(
                        "none",
                        0,
                        event.getPlayer().locale().toString()
                ));
            } else {
                SkyApi.getInstance().getDatabaseManager().getPlayerManager().getPlayerLocale(event.getPlayer().getUniqueId()).whenComplete((locale, ex2) -> {
                    if (ex2 != null) {
                        SLogger.fatal("Failed to get locale for player {} - {}", event.getPlayer().getName(), ex2);
                        return;
                    }

                    SkyApi.getInstance().getPlayerAPI().getPlayerData(event.getPlayer().getUniqueId());
                });
            }
        });

        SkyApi.getInstance().getPlayerAPI().cacheData(event.getPlayer().getUniqueId());

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(event.getPlayer().getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to get player {}'s Island - {}", event.getPlayer().getName(), ex.getMessage());
                return;
            }

            if (island != null) {
                DefencePlacementHandler.addPlacedDefences(event.getPlayer());
                if (Settings.ISLAND_TELEPORT_ON_JOIN.getBoolean()) {

                    World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                    if (world != null) {
                        SkyLocation centerLocation = island.getCenter(world.getName());

                        SkyApi.getInstance().getRegionAPI().modifyWorldBorder(user, island.getCenter(world.getName()), island.getSize());
                        user.teleport(centerLocation);

                    }
                }
            }
        });

        SLogger.info("Initialising Notification Task for {}", event.getPlayer().getName());
        SkyApi.getInstance().getNotificationAPI().createCycle(user);
    }

    @EventHandler
    public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent event) {
        Location bukkitLoc = SpigotAdapter.adapt(SkyApi.getInstance().getRegionAPI().getHubLocation());

        SkyApi.getInstance().getFactionAPI().isInFaction(event.getPlayer().getUniqueId()).whenComplete((is, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to resolve faction island for player {}. Failling back to configured hub location - {}", event.getPlayer().getUniqueId(), ex);
                event.setSpawnLocation(bukkitLoc);
            } else if (!is && event.getSpawnLocation().getWorld().getName().equals(Settings.ISLAND_FACTION_WORLD.getString())) {
                event.setSpawnLocation(bukkitLoc);
            }
        });

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(event.getPlayer().getUniqueId()).whenComplete((is, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to resolve island for player {}. Falling back to configured hub location - {}", event.getPlayer().getUniqueId(), ex);
                event.setSpawnLocation(bukkitLoc);
            } else if (is == null && event.getSpawnLocation().getWorld().getName().equals(Settings.ISLAND_PLAYER_WORLD.getString())) {
                event.setSpawnLocation(bukkitLoc);
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        SkyUser user = SkyApi.getInstance().getUserManager().getUser(event.getPlayer().getUniqueId());
        SLogger.info("Cancelling Notification Task for {}...", event.getPlayer().getName());
        BukkitTask task = SpigotNotificationAPI.tasks.get(event.getPlayer().getUniqueId());
        if (task != null) task.cancel();

        SpigotIslandAPI.modifyDefenceOperation(SpigotFactionAPI.DefenceOperation.DISABLE, event.getPlayer().getUniqueId());

        CooldownManager.ITEMS.remove(user);
        CooldownManager.COMMANDS.remove(user);
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        SkyUser user = SkyApi.getInstance().getUserManager().getUser(event.getPlayer().getUniqueId());
        SkyLocation playerLoc = SpigotAdapter.adapt(event.getPlayer().getLocation());
        if (Settings.ISLAND_TELEPORT_ON_DEATH.getBoolean()) {
            if (SkyApi.getInstance().getRegionAPI().isLocationInRegion(playerLoc, "sfr_player_" + event.getPlayer().getUniqueId()))
                SpigotIslandAPI.modifyDefenceOperation(SpigotFactionAPI.DefenceOperation.DISABLE, event.getPlayer().getUniqueId());
            SkyApi.getInstance().getIslandAPI().getPlayerIsland(event.getPlayer().getUniqueId()).whenComplete((island, ex) -> {
                if (ex != null) {
                    SLogger.fatal("Failed to get player {}'s Island - {}", event.getPlayer().getName(), ex.getMessage());
                    return;
                }

                World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                if (island != null && world != null) {
                    user.teleport(island.getCenter(world.getName()));
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
