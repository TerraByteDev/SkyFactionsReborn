package net.skullian.torrent.skyfactions.event;

import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.defence.DefenceDestructionManager;
import net.skullian.torrent.skyfactions.defence.DefencesFactory;
import net.skullian.torrent.skyfactions.defence.struct.DefenceStruct;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DefenceHandler implements Listener {

    public DefenceDestructionManager manager;

    public DefenceHandler() {
        this.manager = new DefenceDestructionManager();
    }

    @EventHandler
    public void onDefencePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        ItemStack stack = event.getItemInHand();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-name");

        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            String defenceName = container.get(defenceKey, PersistentDataType.STRING);

            DefenceStruct defence = DefencesFactory.defences.get(defenceName);
            if (defence != null) {
                Block placed = event.getBlockPlaced();
                Location belowLoc = placed.getLocation().clone();
                belowLoc.setY(belowLoc.getY() - 1);

                Block belowBlock = placed.getWorld().getBlockAt(belowLoc);
                if (!isAllowedBlock(belowBlock, defence)) {
                    event.setCancelled(true);
                    player.sendMessage(TextUtility.color(defence.getPLACEMENT_BLOCKED_MESSAGE().replace("%server_name%", Messages.SERVER_NAME.getString())));
                }
            } else {
                event.setCancelled(true);
                ErrorHandler.handleError(player, "place your defence", "UNKNOWN_DEFENCE", new IllegalArgumentException("Failed to find defence with the name of " + defenceName));
            }
        }
    }

    @EventHandler
    public void onDefenceBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        Block block = event.getBlock();
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-name");

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        if (container.has(defenceKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMine(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        if (!event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) return;
        if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;

        Block block = player.getTargetBlockExact(4, FluidCollisionMode.NEVER);
        if (block == null) return;
        if (block.getType().equals(Material.AIR)) return;
        manager.updateAndNextPhase(player);

        int blockStage = manager.getBlockStage(block.getLocation());
        manager.sendBlockDamage(player, block.getLocation());
        blockStage = ((blockStage + 1) % 10);
        manager.setBlockStage(block.getLocation(), blockStage);
        if (blockStage == 0) {
            manager.removeBlockStage(block.getLocation());
        }
    }

    private boolean isAllowedBlock(Block block, DefenceStruct defenceStruct) {
        boolean isWhitelist = defenceStruct.isIS_WHITELIST();
        if (isWhitelist) return defenceStruct.getPLACEMENT_LIST().contains(block.getType());
        else return !defenceStruct.getPLACEMENT_LIST().contains(block.getType());
    }
}
