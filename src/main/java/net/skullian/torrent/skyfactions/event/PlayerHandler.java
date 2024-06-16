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
        if (!event.getPlayer().hasPlayedBefore()) {
            LOGGER.info("Player [{}] has not joined before. Syncing database.", event.getPlayer().getName());
            SkyFactionsReborn.db.registerPlayer(event.getPlayer());
        }
    }
}
