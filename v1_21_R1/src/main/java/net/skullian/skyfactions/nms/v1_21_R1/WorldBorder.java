package net.skullian.skyfactions.nms.v1_21_R1;


import net.skullian.skyfactions.util.nms.NMSProvider;
import net.skullian.skyfactions.util.worldborder.AWorldBorder;
import net.skullian.skyfactions.util.worldborder.BorderPos;
import net.skullian.skyfactions.util.worldborder.BorderUpdateAction;
import net.skullian.skyfactions.util.worldborder.ConsumerSupplier;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class WorldBorder extends AWorldBorder {

    private final net.minecraft.world.level.border.WorldBorder border;

    public WorldBorder(Player player) {
        this(new net.minecraft.world.level.border.WorldBorder());
        this.border.world = ((CraftWorld) player.getWorld()).getHandle();
    }

    public WorldBorder(World world) {
        this(((CraftWorld) world).getHandle().getWorldBorder());
    }

    public WorldBorder(net.minecraft.world.level.border.WorldBorder worldBorder) {
        super(
                ConsumerSupplier.of(
                        position -> worldBorder.setCenter(position.x(), position.z()),
                        () -> new BorderPos(worldBorder.getCenterX(), worldBorder.getCenterZ())
                ),
                () -> new BorderPos(worldBorder.getMinX(), worldBorder.getMinZ()),
                () -> new BorderPos(worldBorder.getMaxX(), worldBorder.getMaxZ()),
                ConsumerSupplier.of(worldBorder::setSize, worldBorder::getSize)
        );
        this.border = worldBorder;
    }

    @Override
    public void update(BorderUpdateAction action, Player player) {
        NMSProvider.getNMS_HANDLER().updateWorldBorder(action, player, border);
    }
}