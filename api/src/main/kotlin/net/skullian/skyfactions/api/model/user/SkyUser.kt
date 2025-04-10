package net.skullian.skyfactions.api.model.user

import java.util.UUID

/**
 * The base class for all players.
 * Will be implemented in each platform.
 */
interface SkyUser {

    /**
     * Get the UUID of the user.
     */
    fun getUniqueId(): UUID

    /**
     * Get the player name of this user.
     */
    fun getName(): String

}