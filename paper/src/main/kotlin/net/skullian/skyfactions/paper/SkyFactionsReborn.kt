package net.skullian.skyfactions.paper

import info.preva1l.hooker.Hooker
import info.preva1l.trashcan.flavor.Flavor
import info.preva1l.trashcan.plugin.BasePlugin
import info.preva1l.trashcan.plugin.annotations.PluginEnable
import net.skullian.skyfactions.api.SkyPlatform
import net.skullian.skyfactions.api.SkyApi
import net.skullian.skyfactions.paper.api.PaperSkyApi
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * The main class for the SkyFactions paper implementation.
 * This class is responsible for loading the paper [SkyApi] implementation, etc.
 */
class SkyFactionsReborn: BasePlugin(), SkyPlatform {

    /**
     * Called on server startup.
     * Any startup logic created by plugins like PlugmanX is unsupported.
     */
    @PluginEnable
    fun enable() {
        SkyApi.setInstance(PaperSkyApi())

        // Plugin hooks
        Hooker.register(this, "net.skullian.skyfactions.paper.hooks")
    }

    /**
     * Get the [Flavor] instance.
     */
    fun getFlavor(): Flavor {
        return flavor;
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