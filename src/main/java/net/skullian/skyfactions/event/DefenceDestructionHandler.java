package net.skullian.skyfactions.event;

import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.DefencesFactory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerAnimationEvent;

import java.util.HashSet;
import java.util.Optional;

public class DefenceDestructionHandler implements Listener {

    @EventHandler
    public void onDefenceExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (isDefenceMaterial(block) && DefenceAPI.isDefence(block.getLocation())) {

                Defence defence = DefenceAPI.getLoadedDefence(block.getLocation());
                if (defence != null) defence.damage(Optional.empty());

                event.blockList().remove(block);
            }
        }
    }

    private boolean isDefenceMaterial(Block block) {
        return DefencesFactory.defences.values().stream()
                .anyMatch(defenceStruct -> defenceStruct.getBLOCK_MATERIAL().equals(block.getType().name()));
    }

    private static HashSet<Material> transparentBlocks = new HashSet<>();

    static {
        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.AIR);
    }

    @EventHandler
    public void onBlockBreak(BlockDamageEvent event){

        SkyFactionsReborn.blockService.createBrokenBlock(event.getBlock(), 30);
    }

    @EventHandler
    public void onBreakingBlock(PlayerAnimationEvent event) {
        Player player = event.getPlayer();

        Block block = player.getTargetBlock(transparentBlocks, 5);
        Location blockPos = block.getLocation();

        if (!SkyFactionsReborn.blockService.isBrokenBlock(blockPos)) return;

        ItemStack itemStack = player.getItemInHand();

        double distanceX = blockPos.getX() - player.getLocation().getX();
        double distanceY = blockPos.getY() - player.getLocation().getY();
        double distanceZ = blockPos.getZ() - player.getLocation().getZ();

        if(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ >= 1024.0D) return;
        DefenceDestructionManager.addSlowDig(event.getPlayer(), 200);
        SkyFactionsReborn.blockService.getBrokenBlock(blockPos).incrementDamage(player, 1);
    }


}
