package net.skullian.skyfactions.util.worldborder;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class WorldBorder extends AWorldBorder {

    private final net.minecraft.world.level.border.WorldBorder worldBorder;

    public WorldBorder(World world) {
        this(((CraftWorld) world).getHandle().getWorldBorder());
    }

    public WorldBorder(Player player) {
        this(new net.minecraft.world.level.border.WorldBorder());
        this.worldBorder.world = ((CraftWorld) player.getWorld()).getHandle();
    }

    public WorldBorder(net.minecraft.world.level.border.WorldBorder border) {
        super(
                ConsumerSupplier.of(
                        loc -> border.setCenter(loc.x(), loc.z()),
                        () -> new BorderPos(border.getCenterX(), border.getCenterZ())
                ),
                () -> new BorderPos(border.getMinX(), border.getMinZ()),
                () -> new BorderPos(border.getMaxX(), border.getMaxZ()),
                ConsumerSupplier.of(border::setSize, border::getSize)
        );
        this.worldBorder = border;
    }

    @Override
    public void update(BorderUpdateAction action, Player player) {
        Packet<?> packet = switch(action) {
            case SET -> new ClientboundInitializeBorderPacket(worldBorder);
            case MODIFY_CENTER -> new ClientboundSetBorderCenterPacket(worldBorder);
            case MODIFY_SIZE -> new ClientboundSetBorderSizePacket(worldBorder);
        };

        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public record BorderPos(double x, double z) {
        public static BorderPos of(Location location) {
            return new BorderPos(location.getX(), location.getZ());
        }
    }

}
