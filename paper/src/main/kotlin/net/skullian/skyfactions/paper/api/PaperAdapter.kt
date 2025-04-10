package net.skullian.skyfactions.paper.api

import net.skullian.skyfactions.api.model.SkyLocation
import net.skullian.skyfactions.api.model.user.SkyUser
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Adapts platform classes and SkyFactions platform-independent classes.
 */
object PaperAdapter {

    /**
     * Convert a [SkyLocation] to a Bukkit [Location].
     */
    fun location(location: SkyLocation): Location {
        return Location(
            Bukkit.getWorld(location.worldName) ?: throw IllegalArgumentException("Unknown world ${location.worldName}!"),
            location.x,
            location.y,
            location.z,
            location.yaw,
            location.pitch
        )
    }

    /**
     * Convert a Bukkit [Location] to a [SkyLocation].
     */
    fun location(location: Location): SkyLocation {
        return SkyLocation(
            location.world.name,
            location.x,
            location.y,
            location.z,
            location.yaw,
            location.pitch
        )
    }

    /**
     * Convert a [SkyUser] to a Bukkit [Player].
     */
    fun player(user: SkyUser): Player {
        return Bukkit.getPlayer(user.getUniqueId()) ?: error("Attempted to fetch Player instance of SkyUser [${user.getUniqueId()}] while offline!")
    }
}