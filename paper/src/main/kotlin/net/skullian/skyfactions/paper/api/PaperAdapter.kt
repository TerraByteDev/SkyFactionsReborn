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
    fun SkyLocation.toBukkitLocation(): Location {
        return Location(
            Bukkit.getWorld(worldName) ?: throw IllegalArgumentException("Unknown world $worldName!"),
            x,
            y,
            z,
            yaw,
            pitch
        )
    }

    /**
     * Convert a Bukkit [Location] to a [SkyLocation].
     */
    fun Location.toSkyLocation(): SkyLocation {
        return SkyLocation(
            world.name,
            x,
            y,
            z,
            yaw,
            pitch
        )
    }

    /**
     * Convert a [SkyUser] to a Bukkit [Player].
     */
    fun SkyUser.toBukkitPlayer(): Player {
        return Bukkit.getPlayer(getUniqueId()) ?: error("Attempted to fetch Player instance of SkyUser [${getUniqueId()}] while offline!")
    }
}