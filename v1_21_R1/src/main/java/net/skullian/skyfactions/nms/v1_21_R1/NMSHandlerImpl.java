package net.skullian.skyfactions.nms.v1_21_R1;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.border.WorldBorder;
import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.nms.NMSHandler;
import net.skullian.skyfactions.common.util.worldborder.BorderUpdateAction;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class NMSHandlerImpl implements NMSHandler {

    public NMSHandlerImpl() {
        SLogger.setup("<#05eb2f>v1_21_R1<#4294ed> NMSHandler initialized.", false);
    }

    @Override
    public void updateWorldBorder(BorderUpdateAction action, SkyUser player, Object border) {
        Player bukkitPlayer = SpigotAdapter.adapt(player).getPlayer();
        if (bukkitPlayer != null) {
            WorldBorder worldBorder = (WorldBorder) border;
            Packet<?> packet = switch(action) {
                case SET -> new ClientboundInitializeBorderPacket(worldBorder);
                case MODIFY_CENTER -> new ClientboundSetBorderCenterPacket(worldBorder);
                case MODIFY_SIZE -> new ClientboundSetBorderSizePacket(worldBorder);
            };

            ((CraftPlayer) bukkitPlayer).getHandle().connection.send(packet);
        } else {
            SLogger.warn("Attempted to update an offline player's world border. UUID: {}", player.getUniqueId());
        }
    }

    @Override
    public void spawnHologram(DefenceTextHologram hologram) {
        World bukkitWorld = Bukkit.getWorld(hologram.getLocation().getWorldName());

        if (bukkitWorld != null) {
            Display.TextDisplay textDisplay = new Display.TextDisplay(
                    EntityType.TEXT_DISPLAY,
                    ((CraftWorld) bukkitWorld).getHandle()
            );
            textDisplay.setPos(hologram.getLocation().getX(), hologram.getLocation().getY(), hologram.getLocation().getZ());
            textDisplay.setNoGravity(true);
            textDisplay.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);
            textDisplay.setTextOpacity(hologram.getTextOpacity());

            ServerLevel level = ((CraftWorld) bukkitWorld).getHandle();
            textDisplay.setLevel(level);

            List<SynchedEntityData.DataValue<?>> packedEntityData = textDisplay.getEntityData().packAll();
            if (packedEntityData != null) {
                hologram.setEntity(textDisplay);
                hologram.getViewers()
                        .forEach((user) -> {
                            Player player = SpigotAdapter.adapt(user).getPlayer();
                            if (player == null) return;

                            textDisplay.setText(Component.literal(LegacyComponentSerializer.legacySection().serialize(hologram.createText(user))));

                            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
                            connection.send(new ClientboundAddEntityPacket(textDisplay, 0, new BlockPos(hologram.getLocation().getBlockX(), hologram.getLocation().getBlockY(), hologram.getLocation().getBlockZ())));
                            connection.send(new ClientboundSetEntityDataPacket(textDisplay.getId(), packedEntityData));
                        });
            } else {
                SLogger.warn("Failed to pack entity data for defence hologram!");
            }
        } else {
            SLogger.warn("Attempted to spawn hologram in an unknown world: {}", hologram.getLocation().getWorldName());
        }
    }

    @Override
    public void updateHologram(DefenceTextHologram hologram) {
        Display.TextDisplay textDisplay = (Display.TextDisplay) hologram.getEntity();
        textDisplay.setPos(hologram.getLocation().getX(), hologram.getLocation().getY(), hologram.getLocation().getZ());
        textDisplay.setNoGravity(true);
        textDisplay.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        textDisplay.setTextOpacity(hologram.getTextOpacity());

        List<SynchedEntityData.DataValue<?>> packedEntityData = textDisplay.getEntityData().packAll();
        if (packedEntityData != null) {
            hologram.getViewers()
                    .forEach((user) -> {
                        Player player = SpigotAdapter.adapt(user).getPlayer();
                        if (player == null) return;

                        textDisplay.setText(Component.literal(LegacyComponentSerializer.legacySection().serialize(hologram.createText(user))));

                        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
                        connection.send(new ClientboundSetEntityDataPacket(textDisplay.getId(), packedEntityData));
                    });
        } else {
            SLogger.warn("Failed to pack entity data for defence hologram!");
        }
    }

    @Override
    public void removeHologram(DefenceTextHologram hologram) {
        Display.TextDisplay textDisplay = (Display.TextDisplay) hologram.getEntity();

        hologram.getViewers()
                .forEach((user) -> {
                    Player player = SpigotAdapter.adapt(user).getPlayer();
                    if (player == null) return;

                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
                    connection.send(new ClientboundRemoveEntitiesPacket(textDisplay.getId()));
                });
    }
}
