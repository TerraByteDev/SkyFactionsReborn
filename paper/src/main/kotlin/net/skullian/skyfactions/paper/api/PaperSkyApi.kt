package net.skullian.skyfactions.paper.api

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.skullian.skyfactions.api.SkyApi
import net.skullian.skyfactions.api.SkyPlatform
import net.skullian.skyfactions.api.event.bus.EventBus
import net.skullian.skyfactions.api.service.UserService
import net.skullian.skyfactions.common.eventbus.EventBusImpl
import net.skullian.skyfactions.common.libraries.flavor.Flavor
import net.skullian.skyfactions.common.libraries.flavor.FlavorOptions
import net.skullian.skyfactions.paper.SkyFactionsReborn
import net.skullian.skyfactions.paper.service.PaperUserService

/**
 * The paper implementation of [SkyApi].
 */
class PaperSkyApi : SkyApi {

    private lateinit var platform: SkyPlatform
    private lateinit var audience: BukkitAudiences

    private val eventBus = EventBusImpl()
    private lateinit var flavor: Flavor

    override fun onEnable() {
        this.platform = SkyFactionsReborn.getInstance()
        this.audience = BukkitAudiences.create(SkyFactionsReborn.getInstance())

        this.flavor = Flavor.create<PaperSkyApi>(
            options = FlavorOptions(
                "net.skullian.skyfactions.paper.service",
            )
        )

        flavor.startup()
    }

    override fun onDisable() {
        flavor.close()

        this.audience.close()
    }

    // ------- PROVIDERS ------- //

    override fun getPlatform(): SkyPlatform {
        return platform
    }

    // ------- Services ------- //

    override fun getUserService(): UserService {
        println(flavor.services.size)
        return flavor.services[PaperUserService::class] as UserService
    }

    // ------ Components ------ //

    override fun getEventBus(): EventBus {
        return eventBus
    }

    override fun getConsoleAudience(): Audience {
        return audience.console()
    }
}