package net.skullian.skyfactions.event;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;


public class PlayerHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!SkyFactionsReborn.databaseHandler.isActive()) {
            event.getPlayer().kick();
            throw new RuntimeException("Database is closed! Cannot allow player to join without risking dupes.");
        };

        SkyFactionsReborn.databaseHandler.playerIsRegistered(event.getPlayer()).whenComplete((isRegistered, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            if (!isRegistered) {
                SLogger.info("Player [{}] has not joined before. Syncing with database.", event.getPlayer().getName());
                SkyFactionsReborn.databaseHandler.registerPlayer(event.getPlayer());
            }
        });

        IslandAPI.cacheData(event.getPlayer());

        SkyFactionsReborn.databaseHandler.getPlayerIsland(event.getPlayer().getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to get player {}'s Island - {}", event.getPlayer().getName(), ex.getMessage());
                ex.printStackTrace();
                return;
            }

            if (island != null) {
                DefenceHandler.addPlacedDefences(event.getPlayer());
                if (Settings.ISLAND_TELEPORT_ON_JOIN.getBoolean()) {

                    World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                    if (world != null) {
                        Location centerLocation = island.getCenter(world);

                        IslandAPI.handlePlayerJoinBorder(event.getPlayer(), island);
                        IslandAPI.teleportPlayerToLocation(event.getPlayer(), centerLocation);

                    }
                }
            }
        });

        SLogger.info("Initialising Notification Task for {}", event.getPlayer().getName());
        NotificationAPI.createCycle(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        SLogger.info("Cancelling Notification Task for {}...", event.getPlayer().getName());
        BukkitTask task = NotificationAPI.tasks.get(event.getPlayer().getUniqueId());
        if (task != null) task.cancel();

        IslandAPI.modifyDefenceOperation(FactionAPI.DefenceOperation.DISABLE, event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        if (Settings.ISLAND_TELEPORT_ON_DEATH.getBoolean()) {
            if (FactionAPI.isLocationInRegion(event.getPlayer().getLocation(), event.getPlayer().getUniqueId().toString())) IslandAPI.modifyDefenceOperation(FactionAPI.DefenceOperation.DISABLE, event.getPlayer().getUniqueId());
            SkyFactionsReborn.databaseHandler.getPlayerIsland(event.getPlayer().getUniqueId()).whenComplete((island, ex) -> {
                if (ex != null) {
                    SLogger.fatal("Failed to get player {}'s Island - {}", event.getPlayer().getName(), ex.getMessage());
                    ex.printStackTrace();
                    return;
                }

                World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                if (island != null && world != null) {
                    System.out.println("DEBUG - TELEPORTING");
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
                Messages.NETHER_PORTALS_BLOCKED.send(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }
}
