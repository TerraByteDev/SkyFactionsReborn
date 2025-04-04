package net.skullian.skyfactions.paper.service

import net.skullian.skyfactions.api.library.flavor.service.Service
import net.skullian.skyfactions.api.model.user.SkyUser
import net.skullian.skyfactions.api.service.UserService
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * The paper implementation of [UserService].
 */
@Service
class PaperUserService : UserService {

    private val usersNameCache: MutableMap<String, SkyUser> = ConcurrentHashMap()
    private val usersUUIDCache: MutableMap<UUID, SkyUser> = ConcurrentHashMap()

    override fun getAllUsers(): List<SkyUser> {
        TODO("Not yet implemented")
    }

    override fun getUser(name: String): Optional<SkyUser> {
        TODO("Not yet implemented")
    }

    override fun getUser(uuid: UUID): Optional<SkyUser> {
        TODO("Not yet implemented")
    }

    /**
     * Cache the [SkyUser] instance into [usersNameCache] and [usersUUIDCache].
     */
    fun cache(user: SkyUser) {
        usersNameCache[user.getName()] = user
        usersUUIDCache[user.getUniqueId()] = user
    }

    /**
     * Invalidate a cached [SkyUser] from the [usersNameCache] and [usersUUIDCache],
     * if present.
     */
    fun invalidate(user: SkyUser) {
        usersNameCache.remove(user.getName())
        usersUUIDCache.remove(user.getUniqueId())
    }
}