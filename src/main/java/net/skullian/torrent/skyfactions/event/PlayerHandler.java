package net.skullian.torrent.skyfactions.event;

import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.IslandAPI;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.island.PlayerIsland;
import net.skullian.torrent.skyfactions.notification.NotificationHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Log4j2(topic = "SkyFactionsReborn")
public class PlayerHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SkyFactionsReborn.db.playerIsRegistered(event.getPlayer()).thenAccept(isRegistered -> {
            if (!isRegistered) {
                LOGGER.info("Player [{}] has not joined before. Syncing with database.", event.getPlayer().getName());
                SkyFactionsReborn.db.registerPlayer(event.getPlayer()).exceptionally(ex -> {
                    ex.printStackTrace();
                    LOGGER.fatal("Failed to sync player [{}] with database!", event.getPlayer().getName());
                    return null;
                });
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            LOGGER.fatal("Failed to check if player [{}] was registered with SkyFactionsReborn!", event.getPlayer().getName());
            return null;
        });

        if (Settings.ISLAND_TELEPORT_ON_JOIN.getBoolean()) {
            PlayerIsland island = SkyFactionsReborn.db.getPlayerIsland(event.getPlayer()).join();
            if (island != null) {
                World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                if (world != null) {
                    Location centerLocation = island.getCenter(world);
                    IslandAPI.teleportPlayerToLocation(event.getPlayer(), centerLocation);
                }
            }
        }

        LOGGER.info("Initialising Notification Task for {}", event.getPlayer().getName());
        NotificationHandler.createCycle(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        LOGGER.info("Cancelling Notification Task for {}...", event.getPlayer().getName());
        NotificationHandler.tasks.get(event.getPlayer().getUniqueId()).cancel();
    }
}
