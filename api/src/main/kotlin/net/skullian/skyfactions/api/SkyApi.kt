package net.skullian.skyfactions.api

import net.kyori.adventure.audience.Audience
import net.skullian.skyfactions.api.event.bus.EventBus

/**
 * The main API class for SkyFactions.
 * This class provides access to the various components of the API.
 */
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

    private fun onEnable() {}

    private fun onDisable() {}

    /**
     * Get the [SkyPlatform] instance.
     * This provides useful methods to interact with the platform.
     */
    fun getPlatform(): SkyPlatform

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