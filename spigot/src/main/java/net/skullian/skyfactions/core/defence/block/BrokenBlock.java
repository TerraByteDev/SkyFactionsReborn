package net.skullian.skyfactions.core.defence.block;

//import org.bukkit.craftbukkit.entity.CraftPlayer;


//import net.minecraft.core.BlockPos;
//import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;

public class BrokenBlock {

    /*private int time;
    private int oldAnimation;
    private double damage = -1;
    private Block block;
    private Date lastDamage;

    public BrokenBlock(Block block, int time) {
        this.block = block;
        this.time = time;
        lastDamage = new Date();
    }

    public void incrementDamage(Player from, double multiplier) {
        if (isBroken()) return;

        damage += multiplier;
        int animation = getAnimation();

        if (animation != oldAnimation) {
            if (animation < 10) {
                sendBreakPacket(animation, from);
                lastDamage = new Date();
            } else {
                breakBlock(from);
                return;
            }
        }

        oldAnimation = animation;
    }

    public boolean isBroken() {
        return getAnimation() >= 10;
    }

    public void breakBlock(Player breaker) {
        destroyBlockObject();
        playBreakSound();
        if (breaker == null) return;
        breaker.breakBlock(block);
    }

    public void playBreakSound() {
        block.getWorld().playSound(block.getLocation(),  block.getBlockSoundGroup().getBreakSound(), 1.0f, 1.0f);
    }

    public void destroyBlockObject() {
        sendBreakPacket(-1, null);
    }

    public int getAnimation() {
        return (int) (damage / time * 11) - 1;
    }

    public void sendBreakPacket(int animation, Player breaker) {
        if (breaker != null) {
            ((CraftPlayer) breaker).getHandle().connection.sendPacket(
                    new ClientboundBlockDestructionPacket(
                            breaker.getEntityId(),
                            new BlockPos(block.getX(), block.getY(), block.getZ()),
                            animation
                    )
            );
        } else {
            block.getLocation().getNearbyPlayers(120).forEach((player) -> {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, new ClientboundBlockDestructionPacket(
                        getBlockEntityId(block),
                        new BlockPos(block.getX(), block.getY(), block.getZ()),
                        animation
                ));
            });
        }

        /*WrapperPlayServerBlockBreakAnimation packet = new WrapperPlayServerBlockBreakAnimation(
                breaker != null ? breaker.getEntityId() : getBlockEntityId(block),
                getBlockPosition(block),
                (byte) animation
        );
        PacketEvents.getAPI().getPlayerManager().sendPacket(breaker, packet);*/

        /*block.getLocation().getNearbyPlayers(120).forEach((player) -> {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        });
    }

    private int getBlockEntityId(Block block){
        return ((block.getX() & 0xFFF) << 20 | (block.getZ() & 0xFFF) << 8) | (block.getY() & 0xFF);
    }

    private Vector3i getBlockPosition(Block block){
        return new Vector3i(block.getX(), block.getY(), block.getZ());
    }*/
}
