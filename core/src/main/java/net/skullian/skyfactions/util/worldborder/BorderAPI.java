package net.skullian.skyfactions.util.worldborder;

import net.skullian.skyfactions.util.worldborder.persistence.WBData;
import org.bukkit.entity.Player;

public interface BorderAPI {

    void resetBorder(Player player);

    void setWorldBorder(Player player, double radius, BorderPos location);

    public interface PersistentBorderAPI extends BorderAPI {
        WBData getWBData(Player player);
    }

}
