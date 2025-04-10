package net.skullian.skyfactions.paper.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

/**
 * Handles player data fetching.
 */
class UserListener : Listener {

    /**
     * Listens for the player login event and fetches data, etc.
     */
    @EventHandler
    fun onPreLogin(event: AsyncPlayerPreLoginEvent) {

    }
}