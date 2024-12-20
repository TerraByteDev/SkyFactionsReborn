package net.skullian.skyfactions.nms.v1_21_R3;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.border.WorldBorder;
import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.nms.NMSHandler;
import net.skullian.skyfactions.common.util.worldborder.BorderUpdateAction;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHandlerImpl implements NMSHandler {

    public NMSHandlerImpl() {
        SLogger.setup("v1_21_R3 NMSHandler initialized.", false);
    }

    @Override
    public void updateWorldBorder(BorderUpdateAction action, SkyUser player, Object border) {
        WorldBorder worldBorder = (WorldBorder) border;
        Packet<?> packet = switch(action) {
            case SET -> new ClientboundInitializeBorderPacket(worldBorder);
            case MODIFY_CENTER -> new ClientboundSetBorderCenterPacket(worldBorder);
            case MODIFY_SIZE -> new ClientboundSetBorderSizePacket(worldBorder);
        };

        ((CraftPlayer) SpigotAdapter.adapt(player).getPlayer()).getHandle().connection.send(packet);
    }

    @Override
    public void spawnHologram(DefenceTextHologram hologram) {
        Display.TextDisplay textDisplay = new Display.TextDisplay(
                EntityType.TEXT_DISPLAY,
                ((CraftWorld) Bukkit.getWorld(hologram.getLocation().getWorldName())).getHandle()
        );
        textDisplay.setPos(hologram.getLocation().getX(), hologram.getLocation().getY(), hologram.getLocation().getZ());
        textDisplay.setNoGravity(true);
        textDisplay.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);
        textDisplay.setTextOpacity(hologram.getTextOpacity());

        ServerLevel level = ((CraftWorld) Bukkit.getWorld(hologram.getLocation().getWorldName())).getHandle();
        textDisplay.setLevel(level);

        hologram.setEntity(textDisplay);
        hologram.getViewers().stream()
                .forEach((user) -> {
                    if (!user.isOnline()) return;
                    Player player = SpigotAdapter.adapt(user).getPlayer();
                    textDisplay.setText(Component.literal(LegacyComponentSerializer.legacySection().serialize(hologram.createText(user))));

                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
                    connection.send(new ClientboundAddEntityPacket(textDisplay, 0, new BlockPos(hologram.getLocation().getBlockX(), hologram.getLocation().getBlockY(), hologram.getLocation().getBlockZ())));
                    connection.send(new ClientboundSetEntityDataPacket(textDisplay.getId(), textDisplay.getEntityData().packAll()));
                });
    }

    @Override
    public void updateHologram(DefenceTextHologram hologram) {
        Display.TextDisplay textDisplay = (Display.TextDisplay) hologram.getEntity();
        textDisplay.setPos(hologram.getLocation().getX(), hologram.getLocation().getY(), hologram.getLocation().getZ());
        textDisplay.setNoGravity(true);
        textDisplay.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        textDisplay.setTextOpacity(hologram.getTextOpacity());

        hologram.getViewers().stream()
                .forEach((user) -> {
                    Player player = SpigotAdapter.adapt(user).getPlayer();
                    textDisplay.setText(Component.literal(LegacyComponentSerializer.legacySection().serialize(hologram.createText(user))));

                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
                    connection.send(new ClientboundSetEntityDataPacket(textDisplay.getId(), textDisplay.getEntityData().packAll()));
                });
    }

    @Override
    public void removeHologram(DefenceTextHologram hologram) {
        Display.TextDisplay textDisplay = (Display.TextDisplay) hologram.getEntity();

        hologram.getViewers().stream()
                .forEach((user) -> {
                    Player player = SpigotAdapter.adapt(user).getPlayer();

                    ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
                    connection.send(new ClientboundRemoveEntitiesPacket(textDisplay.getId()));
                });
    }
}
