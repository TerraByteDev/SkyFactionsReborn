package net.skullian.skyfactions.paper.api

import info.preva1l.trashcan.flavor.annotations.Close
import info.preva1l.trashcan.flavor.annotations.Configure
import info.preva1l.trashcan.flavor.annotations.inject.Inject
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.skullian.skyfactions.api.SkyApi
import net.skullian.skyfactions.api.SkyPlatform
import net.skullian.skyfactions.api.database.DatabaseService
import net.skullian.skyfactions.api.event.bus.EventBus
import net.skullian.skyfactions.api.service.UserService
import net.skullian.skyfactions.common.event.bus.EventBusImpl
import net.skullian.skyfactions.paper.SkyFactionsReborn

/**
 * The paper implementation of [SkyApi].
 */
class PaperSkyApi : SkyApi {

    private lateinit var platform: SkyPlatform
    private lateinit var audience: BukkitAudiences

    private val eventBus = EventBusImpl()

    @Inject
    lateinit var plugin: SkyFactionsReborn

    /**
     * Called when the plugin is enabled.
     * This is typically when the server starts, unless
     * you are being an idiot and use plugman.
     */
    @Configure
    fun onEnable() {
        this.platform = SkyFactionsReborn.getInstance()
        this.audience = BukkitAudiences.create(SkyFactionsReborn.getInstance())
    }

    /**
     * Called when the plugin disables, typically when the server stops.
     * This could also be called when the plugin fails to load.
     */
    @Close
    fun onDisable() {
        this.audience.close()
    }

    // ------- PROVIDERS ------- //

    override fun getPlatform(): SkyPlatform {
        return platform
    }

    // ------- Services ------- //

    override fun getUserService(): UserService {
        return plugin.getFlavor().service(UserService::class.java)
    }

    override fun getDatabaseService(): DatabaseService {
        TODO("Not yet implemented")
    }

    // ------ Components ------ //

    override fun getEventBus(): EventBus {
        return eventBus
    }

    override fun getConsoleAudience(): Audience {
        return audience.console()
    }
}