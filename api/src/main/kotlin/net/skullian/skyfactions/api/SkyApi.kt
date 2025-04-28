package net.skullian.skyfactions.api

import info.preva1l.trashcan.flavor.annotations.Service
import net.kyori.adventure.audience.Audience
import net.skullian.skyfactions.api.database.DatabaseService
import net.skullian.skyfactions.api.event.bus.EventBus
import net.skullian.skyfactions.api.model.user.SkyUser
import net.skullian.skyfactions.api.service.UserService

/**
 * The main API class for SkyFactions.
 * This class provides access to the various components of the API.
 */
@Service(
    priority = 1
)
interface SkyApi {

    companion object {
        private var instance: SkyApi? = null

        /**
         * Set the instance of SkyApi.
         * This can only be done once, and is done by the [SkyPlatform] instance.
         */
        @JvmStatic
        fun setInstance(newInstance: SkyApi) {
            check(instance == null) { "SkyApi instance has already been set!" }

            this.instance = newInstance
        }

        /**
         * Get the instance of SkyApi.
         * This should only be called after the instance has been set.
         */
        @JvmStatic
        fun getInstance(): SkyApi {
            return instance ?: error("SkyApi instance has not been set!")
        }
    }

    /**
     * Get the [SkyPlatform] instance.
     * This provides useful methods to interact with the platform.
     */
    fun getPlatform(): SkyPlatform

    // ------- Services ------- //

    /**
     * Get the user service.
     * This facilitates fetching [SkyUser] instances.
     */
    fun getUserService(): UserService

    /**
     * Get the database service.
     * This facilitates the saving of all persistent data.
     */
    fun getDatabaseService(): DatabaseService

    // ------ Components ------ //

    /**
     * Returns the EventBus implementation.
     * This allows you to register listeners and emit events.
     *
     * @return The [EventBus] instance.
     */
    fun getEventBus(): EventBus

    /**
     * Returns the console audience, provided by the platform Adventure instance.
     *
     * @return The platform [Audience] instance representing the console.
     */
    fun getConsoleAudience(): Audience
}