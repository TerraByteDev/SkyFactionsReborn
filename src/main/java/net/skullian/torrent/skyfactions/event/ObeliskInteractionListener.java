package net.skullian.torrent.skyfactions.event;

import com.jeff_media.customblockdata.CustomBlockData;
import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.ObeliskConfig;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.gui.obelisk.FactionObeliskUI;
import net.skullian.torrent.skyfactions.gui.obelisk.PlayerObeliskUI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@Log4j2(topic = "SkyFactionsReborn")
public class ObeliskInteractionListener implements Listener {

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString())) && !event.getBlock().getType().equals(Material.BARRIER)) return;

        Block block = event.getBlock();
        Player player = event.getPlayer();

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());

        NamespacedKey typeKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
        NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
        if (!container.has(typeKey, PersistentDataType.STRING) || !container.has(ownerKey, PersistentDataType.STRING)) return;

        event.setCancelled(true);
        Messages.OBELISK_DESTROY_DENY.send(player);
    }

    // Listen for interaction with the Obelisk
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.hasBlock()) return;
        Block block = event.getClickedBlock();
        if (block.getType() != Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString()) && block.getType() != Material.BARRIER) return;
        Player player = event.getPlayer();

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());

        NamespacedKey typeKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
        NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
        if (!container.has(typeKey, PersistentDataType.STRING) || !container.has(ownerKey, PersistentDataType.STRING)) return;

        event.setCancelled(true);
        String type = container.get(typeKey, PersistentDataType.STRING);
        String owner = container.get(ownerKey, PersistentDataType.STRING);

        if (!hasPermissions(player, type, owner)) {
            Messages.OBELISK_ACCESS_DENY.send(player);
        }
    }

    private boolean hasPermissions(Player player, String type, String owner) {
        if (type.equals("player")) {
            if (owner.equals(player.getUniqueId().toString())) {
                PlayerObeliskUI.promptPlayer(player);
                return true;
            }

            return false;
        } else if (type.equals("faction")) {
            Faction faction = FactionAPI.getFaction(player);
            if (faction != null && faction.getName().equalsIgnoreCase(owner)) {
                FactionObeliskUI.promptPlayer(player);
                return true;
            }

            return false;
        }

        return false;
    }
}
