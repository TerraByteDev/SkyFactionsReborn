package net.skullian.skyfactions.api.model.user

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.skullian.skyfactions.api.model.SkyLocation

/**
 * An online user.
 */
interface OnlineSkyUser : SkyUser {

    /**
     * Get the player Adventure [Audience].
     */
    fun getAudience(): Audience

    /**
     * Teleport a player to this [SkyLocation].
     *
     * @param location The location they should be teleported to.
     */
    fun teleport(location: SkyLocation)

    /**
     * Send the player a message.
     *
     * @param message The [Component] that should be sent to the player.
     */
    fun sendMessage(message: Component)

    /**
     * Execute a command as the player.
     *
     * @param command The command to execute.
     */
    fun executeCommand(command: String)

    /**
     * Get the current [SkyLocation] of the player.
     */
    fun getLocation(): SkyLocation

    /**
     * Close the player's current open inventory.
     */
    fun closeInventory()

    /**
     * If the player has a certain permission.
     *
     * @param permission The permission to check
     */
    fun hasPermission(permission: String): Boolean

    /**
     * Play a [Sound] to the player.
     *
     * @param soundIdentifier The [String] sound to play.
     * @param volume The volume to play the sound at.
     * @param pitch The pitch to play the sound at.
     */
    fun playSound(soundIdentifier: String, volume: Float, pitch: Float)

    /**
     * Kick the player with a specific message.
     *
     * @param reason The kick reason, as a [Component]
     */
    fun kick(reason: Component)

}