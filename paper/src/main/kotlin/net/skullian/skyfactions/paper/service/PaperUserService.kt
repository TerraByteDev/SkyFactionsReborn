package net.skullian.skyfactions.paper.service

import net.skullian.skyfactions.api.model.user.SkyUser
import net.skullian.skyfactions.api.service.UserService
import java.util.*

/**
 * The paper implementation of [UserService].
 */
class PaperUserService : UserService {
    override fun getAllUsers(): List<SkyUser> {
        TODO("Not yet implemented")
    }

    override fun getUser(name: String): Optional<SkyUser> {
        TODO("Not yet implemented")
    }

    override fun getUser(uuid: UUID): Optional<SkyUser> {
        TODO("Not yet implemented")
    }
}