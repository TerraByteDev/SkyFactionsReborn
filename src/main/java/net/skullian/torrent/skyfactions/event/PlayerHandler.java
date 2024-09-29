package net.skullian.torrent.skyfactions.event;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.IslandAPI;
import net.skullian.torrent.skyfactions.api.NotificationAPI;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.island.PlayerIsland;
import net.skullian.torrent.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public class PlayerHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SkyFactionsReborn.db.playerIsRegistered(event.getPlayer()).thenAccept(isRegistered -> {
            if (!isRegistered) {
                SLogger.info("Player [{}] has not joined before. Syncing with database.", event.getPlayer().getName());
                SkyFactionsReborn.db.registerPlayer(event.getPlayer()).exceptionally(ex -> {
                    ex.printStackTrace();
                    SLogger.fatal("Failed to sync player [{}] with database!", event.getPlayer().getName());
                    return null;
                });
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            SLogger.fatal("Failed to check if player [{}] was registered with SkyFactionsReborn!", event.getPlayer().getName());
            return null;
        });

        PlayerIsland island = SkyFactionsReborn.db.getPlayerIsland(event.getPlayer().getUniqueId()).join();
        if (island != null) {
            IslandAPI.handleJoinBorder(event.getPlayer(), island);

            if (Settings.ISLAND_TELEPORT_ON_JOIN.getBoolean()) {
                World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                if (world != null) {
                    Location centerLocation = island.getCenter(world);
                    IslandAPI.teleportPlayerToLocation(event.getPlayer(), centerLocation);
                }
            }
        }

        SLogger.info("Initialising Notification Task for {}", event.getPlayer().getName());
        NotificationAPI.createCycle(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        SLogger.info("Cancelling Notification Task for {}...", event.getPlayer().getName());
        NotificationAPI.tasks.get(event.getPlayer().getUniqueId()).cancel();
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        if (Settings.ISLAND_TELEPORT_ON_DEATH.getBoolean()) {
            PlayerIsland island = SkyFactionsReborn.db.getPlayerIsland(event.getPlayer().getUniqueId()).join();
            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (island != null && world != null) {
                event.getPlayer().teleport(island.getCenter(world));
            }
        }
    }
}
