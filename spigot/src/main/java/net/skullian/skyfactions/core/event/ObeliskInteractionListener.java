package net.skullian.skyfactions.core.event;

import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.SpigotFactionAPI;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.ObeliskConfig;
import net.skullian.skyfactions.core.gui.screens.obelisk.FactionObeliskUI;
import net.skullian.skyfactions.core.gui.screens.obelisk.PlayerObeliskUI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.CompletableFuture;


public class ObeliskInteractionListener implements Listener {

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (!block.getType().equals(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString())) && !block.getType().equals(Material.BARRIER))
                continue;

            PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
            NamespacedKey typeKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
            NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
            if (!container.has(typeKey, PersistentDataType.STRING) || !container.has(ownerKey, PersistentDataType.STRING))
                continue;

            event.blockList().remove(block);
            break;
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (!block.getType().equals(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString())) && !block.getType().equals(Material.BARRIER))
                continue;

            PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
            NamespacedKey typeKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
            NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
            if (!container.has(typeKey, PersistentDataType.STRING) || !container.has(ownerKey, PersistentDataType.STRING))
                continue;

            event.blockList().remove(block);
            break;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString())) && !event.getBlock().getType().equals(Material.BARRIER))
            return;

        Block block = event.getBlock();
        Player player = event.getPlayer();

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());

        NamespacedKey typeKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
        NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
        if (!container.has(typeKey, PersistentDataType.STRING) || !container.has(ownerKey, PersistentDataType.STRING))
            return;

        event.setCancelled(true);
        Messages.OBELISK_DESTROY_DENY.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
    }

    // Listen for interaction with the Obelisk
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.hasBlock()) return;
        Block block = event.getClickedBlock();
        if (block.getType() != Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString()) && block.getType() != Material.BARRIER)
            return;
        Player player = event.getPlayer();

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());

        NamespacedKey typeKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
        NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
        if (!container.has(typeKey, PersistentDataType.STRING) || !container.has(ownerKey, PersistentDataType.STRING))
            return;

        event.setCancelled(true);
        String type = container.get(typeKey, PersistentDataType.STRING);
        String owner = container.get(ownerKey, PersistentDataType.STRING);

        hasPermissions(player, type, owner).thenAccept((has) -> {
            if (!has) {
                Messages.OBELISK_ACCESS_DENY.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
            }
        });
    }

    private CompletableFuture<Boolean> hasPermissions(Player player, String type, String owner) {
        if (type.equals("player")) {
            if (owner.equals(player.getUniqueId().toString())) {
                PlayerObeliskUI.promptPlayer(player);
                return CompletableFuture.completedFuture(true);
            }

            return CompletableFuture.completedFuture(false);
        } else if (type.equals("faction")) {
            return SpigotFactionAPI.getFaction(player.getUniqueId()).thenApply(faction -> {
                if (faction == null) {
                    return false;
                }
                if (faction.getName().equalsIgnoreCase(owner)) {
                    FactionObeliskUI.promptPlayer(player);
                    return true;
                }
                return false;
            });
        }

        return CompletableFuture.completedFuture(false);
    }
}
