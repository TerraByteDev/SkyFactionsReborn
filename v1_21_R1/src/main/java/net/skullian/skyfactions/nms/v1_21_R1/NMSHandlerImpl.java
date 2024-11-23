package net.skullian.skyfactions.nms.v1_21_R1;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.world.level.border.WorldBorder;
import net.skullian.skyfactions.util.nms.NMSHandler;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.worldborder.BorderUpdateAction;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHandlerImpl implements NMSHandler {

    public NMSHandlerImpl() {
        SLogger.info("NMSHandler initialized.");
    }

    @Override
    public void updateWorldBorder(BorderUpdateAction action, Player player, Object border) {
        WorldBorder worldBorder = (WorldBorder) border;
        Packet<?> packet = switch(action) {
            case SET -> new ClientboundInitializeBorderPacket(worldBorder);
            case MODIFY_CENTER -> new ClientboundSetBorderCenterPacket(worldBorder);
            case MODIFY_SIZE -> new ClientboundSetBorderSizePacket(worldBorder);
        };

        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}