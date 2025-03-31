package net.skullian.skyfactions.paper

import net.skullian.skyfactions.api.SkyPlatform
import net.skullian.skyfactions.api.SkyApi
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * The main class for the SkyFactions paper implementation.
 * This class is responsible for loading the paper [SkyApi] implementation, etc.
 */
class SkyFactionsReborn: JavaPlugin(), SkyPlatform {
    override fun onDisable() {
        TODO("Not yet implemented")
    }

    override fun onEnable() {
        TODO("Not yet implemented")
    }

    override fun getConfigDirectory(): File {
        return getConfigDirectory()
    }

    override fun isPluginEnabled(pluginName: String): Boolean {
        return server.pluginManager.isPluginEnabled(pluginName)
    }
}