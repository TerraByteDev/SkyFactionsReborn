package net.skullian.skyfactions.paper.model.user

import net.skullian.skyfactions.api.model.user.SkyUser
import org.bukkit.OfflinePlayer
import java.util.*

/**
 * The paper implementation of [SkyUser].
 */
class PaperSkyUser(player: OfflinePlayer) : SkyUser {

    private var offlinePlayer: OfflinePlayer = player

    override fun getUniqueId(): UUID {
        return offlinePlayer.uniqueId
    }

    override fun getName(): String {
        return offlinePlayer.name!!
    }

}