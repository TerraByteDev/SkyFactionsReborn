package net.skullian.skyfactions.block;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BrokenBlock {

    private int time;
    private int oldAnimation;
    private double damage = -1;
    private final Block block;
    private long lastDamage;

    public BrokenBlock(Block block, int time) {
        this.block = block;
        this.time = time;
        lastDamage = System.currentTimeMillis();
    }

    public void incrementDamage(Player from, double multiplier) {
        if (isBroken()) return;
    }

    public boolean isBroken() {
        return getAnimation() >= 10;
    }

    public void breakBlock(Player breaker) {
        destroyBlockObject();
        block.getWorld().playSound(block.getLocation(), block.getBlockSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1, 1);
        if (breaker == null) return;
        breaker.breakBlock(block);
    }

    public void destroyBlockObject() {
        sendBreakPacket(-1, block);
        //BrokenBlockService.removeBrokenBlock(block.getLocation());
    }

    public int getAnimation() {
        return (int) (damage / time * 11) - 1;
    }

    public void sendBreakPacket(int animation, Block block) {
        WrapperPlayServerBlockBreakAnimation packet = new WrapperPlayServerBlockBreakAnimation(
                getBlockEntitiyId(block),
                new Vector3i(block.getX(), block.getY(), block.getZ()),
                (byte) animation
        );

        PacketEvents.getAPI().getPlayerManager();
    }

    private BlockPosition getBlockPosition(Block block) {
        return new BlockPosition() {
            @Override
            public int blockX() {
                return block.getX();
            }

            @Override
            public int blockY() {
                return block.getY();
            }

            @Override
            public int blockZ() {
                return block.getZ();
            }
        };
    }

    private int getBlockEntitiyId(Block block) {
        return ((block.getX() & 0xFFF) << 20 | (block.getZ() & 0xFFF) << 8) | (block.getY() & 0xFF);
    }
}
