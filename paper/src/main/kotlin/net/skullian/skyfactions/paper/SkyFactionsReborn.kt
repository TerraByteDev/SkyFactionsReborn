package net.skullian.skyfactions.paper

import net.skullian.skyfactions.api.SkyPlatform
import net.skullian.skyfactions.api.SkyApi
import net.skullian.skyfactions.paper.api.PaperSkyApi
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * The main class for the SkyFactions paper implementation.
 * This class is responsible for loading the paper [SkyApi] implementation, etc.
 */
class SkyFactionsReborn: JavaPlugin(), SkyPlatform {

    override fun onEnable() {
        SkyApi.setInstance(PaperSkyApi())

        SkyApi.getInstance().onEnable()
    }

    override fun onDisable() {
        SkyApi.getInstance().onDisable()
    }

    override fun getConfigDirectory(): File {
        return getConfigDirectory()
    }

    override fun isPluginEnabled(pluginName: String): Boolean {
        return server.pluginManager.isPluginEnabled(pluginName)
    }

    companion object {
        /**
         * Get the [JavaPlugin] instance of SkyFactionsReborn.
         * We only have this in case we want to get the JavaPlugin class,
         * rather than the [SkyPlatform] instance which wouldn't
         * give us access to the [JavaPlugin] methods.
         *
         * [SkyApi.getPlatform] would be used mainly for the API (externally).
         */
        @JvmStatic
        fun getInstance(): SkyFactionsReborn {
            return getPlugin(SkyFactionsReborn::class.java)
        }
    }
}