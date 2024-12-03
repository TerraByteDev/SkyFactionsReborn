package net.skullian.skyfactions.common.util.nms;

import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.worldborder.BorderUpdateAction;

public interface NMSHandler {
    void updateWorldBorder(BorderUpdateAction action, SkyUser player, Object worldBorder);

    void spawnHologram(DefenceTextHologram hologram);

    void updateHologram(DefenceTextHologram hologram);

    void removeHologram(DefenceTextHologram hologram);

}
