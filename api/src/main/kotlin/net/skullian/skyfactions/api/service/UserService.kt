package net.skullian.skyfactions.api.service

import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.skullian.skyfactions.api.model.user.SkyUser
import java.util.Optional
import java.util.UUID

/**
 * The user service, for fetching [SkyUser] instances.
 */
@Service
@IgnoreAutoScan
interface UserService {

    /**
     * Get all cached users.
     */
    fun getAllUsers(): List<SkyUser>

    /**
     * Get (or create) a new [SkyUser] from their player name.
     */
    fun getUser(name: String): Optional<SkyUser>

    /**
     * Get (or create) a new [SkyUser] from their [UUID].
     */
    fun getUser(uuid: UUID): Optional<SkyUser>

}