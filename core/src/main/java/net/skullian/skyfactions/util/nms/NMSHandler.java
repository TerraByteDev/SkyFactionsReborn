package net.skullian.skyfactions.util.nms;

import net.skullian.skyfactions.util.worldborder.BorderUpdateAction;
import org.bukkit.entity.Player;

public interface NMSHandler {
    void updateWorldBorder(BorderUpdateAction action, Player player, Object worldBorder);
}
