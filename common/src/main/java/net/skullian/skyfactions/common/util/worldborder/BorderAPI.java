package net.skullian.skyfactions.common.util.worldborder;

import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.worldborder.persistence.WBData;

public interface BorderAPI {

    /**
     * Reset a player's world border.
     *
     * @param player Player in question.
     */
    void resetBorder(SkyUser player);

    /**
     * Modify a player's individual world border.
     *
     * @param player Player in question.
     * @param radius New radius of the world border.
     * @param location New central location of the world border.
     */
    void setWorldBorder(SkyUser player, double radius, BorderPos location);

    interface PersistentBorderAPI extends BorderAPI {
        WBData getWBData(SkyUser player);
    }

}
