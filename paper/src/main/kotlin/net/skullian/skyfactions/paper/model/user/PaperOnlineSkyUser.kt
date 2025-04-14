package net.skullian.skyfactions.paper.model.user

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.skullian.skyfactions.api.model.SkyLocation
import net.skullian.skyfactions.api.model.user.OnlineSkyUser
import net.skullian.skyfactions.paper.api.PaperAdapter
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerKickEvent.Cause
import java.util.*

/**
 * A Paper implementation of an online user.
 */
class PaperOnlineSkyUser(val player: Player) : OnlineSkyUser {

    override fun getAudience(): Audience {
        return player
    }

    override fun teleport(location: SkyLocation) {
        player.teleport(
            PaperAdapter.location(location)
        )
    }

    override fun sendMessage(message: Component) {
        player.sendMessage(message)
    }

    override fun executeCommand(command: String) {
        player.performCommand(command)
    }

    override fun getLocation(): SkyLocation {
        return PaperAdapter.location(player.location)
    }

    override fun closeInventory() {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
    }

    override fun hasPermission(permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override fun playSound(soundIdentifier: String, volume: Float, pitch: Float) {
        val sound = Sound.sound(Key.key(soundIdentifier), Sound.Source.MASTER, volume, pitch)
        getAudience().playSound(sound)
    }

    override fun kick(reason: Component) {
        player.kick(reason, Cause.PLUGIN)
    }

    override fun getUniqueId(): UUID {
        return player.uniqueId
    }

    override fun getName(): String {
        return player.name
    }
}