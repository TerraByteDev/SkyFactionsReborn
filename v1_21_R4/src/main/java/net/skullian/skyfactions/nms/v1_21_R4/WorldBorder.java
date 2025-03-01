package net.skullian.skyfactions.nms.v1_21_R4;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.worldborder.AWorldBorder;
import net.skullian.skyfactions.common.util.worldborder.BorderPos;
import net.skullian.skyfactions.common.util.worldborder.BorderUpdateAction;
import net.skullian.skyfactions.common.util.worldborder.ConsumerSupplier;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Objects;

public class WorldBorder extends AWorldBorder {

    private final net.minecraft.world.level.border.WorldBorder border;

    public WorldBorder() {
        this(new net.minecraft.world.level.border.WorldBorder());
    }

    public WorldBorder(SkyUser player) {
        this(new net.minecraft.world.level.border.WorldBorder());

        Player spigotPlayer = SpigotAdapter.adapt(player).getPlayer();
        if (spigotPlayer != null) {
            this.border.world = ((CraftWorld) spigotPlayer.getWorld()).getHandle();
        } else {
            SLogger.warn("Attempted to create a world border for an offline player. UUID: {}", player.getUniqueId());
        }
    }

    public WorldBorder(String world) {
        this(((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(world))).getHandle().getWorldBorder());
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
    public void update(BorderUpdateAction action, SkyUser player) {
        SkyApi.getInstance().getNMSProvider().getInstance().updateWorldBorder(action, player, border);
    }
}