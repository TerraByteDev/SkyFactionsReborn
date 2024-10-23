package net.skullian.skyfactions.event;

import net.skullian.skyfactions.api.DefenceAPI;
import net.skullian.skyfactions.defence.DefencesFactory;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;

public class DefenceDestructionHandler implements Listener {

    @EventHandler
    public void onDefenceExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (isDefenceMaterial(block) && DefenceAPI.isDefence(block.getLocation())) {
                // todo - damage defence
                event.blockList().remove(block);
            }
        }
    }

    private boolean isDefenceMaterial(Block block) {
        return DefencesFactory.defences.values().stream()
                .anyMatch(defenceStruct -> defenceStruct.getBLOCK_MATERIAL().equals(block.getType().name()));
    }


}
