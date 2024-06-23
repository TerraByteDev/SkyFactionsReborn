package net.skullian.torrent.skyfactions.event;

import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

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
    }
}
