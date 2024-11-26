package net.skullian.skyfactions.core.event.defence;

import java.util.Optional;
import java.util.UUID;

import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.core.config.types.DefencesConfig;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.event.armor.ArmorEquipEvent;
import net.skullian.skyfactions.core.gui.screens.defence.DefenceManageUI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;

import net.skullian.skyfactions.core.api.SpigotDefenceAPI;
import net.skullian.skyfactions.common.defence.Defence;
import org.bukkit.event.player.PlayerInteractEvent;

public class DefenceInteractionHandler implements Listener {

    @EventHandler
    public void onDefenceExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (SpigotDefenceAPI.isDefenceMaterial(block) && SpigotDefenceAPI.isDefence(block.getLocation())) {

                Defence defence = SpigotDefenceAPI.getLoadedDefence(block.getLocation());
                if (defence != null) defence.damage(Optional.empty());

                event.blockList().remove(block);
            }
        }
    }

    @EventHandler
    public void onDefenceBreak(BlockBreakEvent event) {
        if (SpigotDefenceAPI.isDefence(event.getBlock().getLocation())) {
            event.setCancelled(true);
            Messages.DEFENCE_DESTROY_DENY.send(event.getPlayer(), event.getPlayer().locale().getLanguage());
        }
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        if (SpigotDefenceAPI.isDefence(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDefenceInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.hasBlock()) return;
        if (!SpigotDefenceAPI.isDefenceMaterial(event.getClickedBlock()) || !SpigotDefenceAPI.isDefence(event.getClickedBlock().getLocation())) return;

        Player player = event.getPlayer();
        Defence defence = SpigotDefenceAPI.getLoadedDefence(event.getClickedBlock().getLocation());

        if (defence != null) {
            if (defence.getData().isIS_FACTION()) {
                SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    } else if (faction == null) return;
                        else if (!faction.getName().equals(defence.getData().getUUIDFactionName())) return;
                        else if (SpigotDefenceAPI.hasPermissions(DefencesConfig.PERMISSION_ACCESS_DEFENCE.getList(), player, faction))
                            Messages.DEFENCE_INSUFFICIENT_PERMISSIONS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));

                    DefenceManageUI.promptPlayer(player, defence.getData(), defence.getStruct(), faction);
                });
            } else {
                if (UUID.fromString(defence.getData().getUUIDFactionName()).equals(player.getUniqueId())) {
                    DefenceManageUI.promptPlayer(player, defence.getData(), defence.getStruct(), null);
                }
            }
        }
    }

    /*private static HashSet<Material> transparentBlocks = new HashSet<>();

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

        double distanceX = blockPos.getX() - player.getLocation().getX();
        double distanceY = blockPos.getY() - player.getLocation().getY();
        double distanceZ = blockPos.getZ() - player.getLocation().getZ();

        if(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ >= 1024.0D) return;
        DefenceDestructionManager.addSlowDig(event.getPlayer(), 200);
        SkyFactionsReborn.blockService.getBrokenBlock(blockPos).incrementDamage(player, 1);
    }*/


}
