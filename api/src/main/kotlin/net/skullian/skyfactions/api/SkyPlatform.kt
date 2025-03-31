package net.skullian.skyfactions.api

import java.io.File

/**
 * This is the root interface for the SkyFactions platform.
 * It is implemented in all platform main classes (JavaPlugin for spigot, ModInitializer for fabric, etc)
 */
interface SkyPlatform {

    /**
     * @return The config directory as a [File].
     */
    fun getConfigDirectory(): File

    /**
     * @return Whether the plugin is enabled or not.
     */
    fun isPluginEnabled(pluginName: String): Boolean

}