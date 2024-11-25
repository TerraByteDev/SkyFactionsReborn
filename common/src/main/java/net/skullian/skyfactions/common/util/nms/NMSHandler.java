package net.skullian.skyfactions.common.util.nms;

import net.skullian.skyfactions.common.util.worldborder.BorderUpdateAction;
import org.bukkit.entity.Player;

public interface NMSHandler {
    void updateWorldBorder(BorderUpdateAction action, Player player, Object worldBorder);
}
