package net.skullian.skyfactions.paper.api

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.skullian.skyfactions.api.SkyApi
import net.skullian.skyfactions.api.SkyPlatform
import net.skullian.skyfactions.api.event.bus.EventBus
import net.skullian.skyfactions.common.eventbus.EventBusImpl
import net.skullian.skyfactions.paper.SkyFactionsReborn

/**
 * The paper implementation of [SkyApi].
 */
class PaperSkyApi : SkyApi {

    private lateinit var platform: SkyPlatform
    private lateinit var audience: BukkitAudiences

    private val eventBus = EventBusImpl()

    override fun onEnable() {
        this.platform = SkyFactionsReborn.getInstance()
        this.audience = BukkitAudiences.create(SkyFactionsReborn.getInstance())
    }

    // -------- PROVIDERS -------- //

    override fun getPlatform(): SkyPlatform {
        return platform
    }

    override fun getEventBus(): EventBus {
        return eventBus
    }

    override fun getConsoleAudience(): Audience {
        return audience.console()
    }
}