package net.skullian.torrent.skyfactions.event;

import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.ObeliskConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
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
        try {
            if (event.getPlayer() == null) return;
            if (!event.getBlock().getType().equals(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString()))) return;

            Block block = event.getBlock();
            Player player = event.getPlayer();

            TileState state = (TileState) block.getState();
            PersistentDataContainer container = state.getPersistentDataContainer();

            NamespacedKey typeKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
            NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
            if (!container.has(typeKey, PersistentDataType.STRING) || !container.has(ownerKey, PersistentDataType.STRING)) return;

            event.setCancelled(true);
            Messages.OBELISK_DESTROY_DENY.send(player);
        } catch (ClassCastException ignored) {}
    }

    // Listen for interaction with the Obelisk
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
            if (!event.hasBlock()) return;
            if (event.getClickedBlock().getType() != Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString())) return;

            Block block = event.getClickedBlock();
            Player player = event.getPlayer();

            TileState state = (TileState) block.getState();
            PersistentDataContainer container = state.getPersistentDataContainer();

            NamespacedKey typeKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
            NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
            if (!container.has(typeKey, PersistentDataType.STRING) || !container.has(ownerKey, PersistentDataType.STRING)) return;
            event.setCancelled(true);

            String type = container.get(typeKey, PersistentDataType.STRING);
            String owner = container.get(ownerKey, PersistentDataType.STRING);

            if (!hasPermissions(player, type, owner)) {
                Messages.OBELISK_ACCESS_DENY.send(player);
            } else {

            }
        } catch (ClassCastException ignored) {}
    }

    @EventHandler
    public void test(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.CHEST) return;
        if (!(event.getBlock().getState() instanceof TileState)) return;
        System.out.println("yay");
        TileState state = (TileState) event.getBlock().getState();
        System.out.println(state);
    }

    @EventHandler
    public void test1(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.BEACON) return;
        if (!(event.getBlock().getState() instanceof TileState)) return;
        System.out.println("BERACON");
        TileState state = (TileState) event.getBlock().getState();
        System.out.println(state);
    }

    private boolean hasPermissions(Player player, String type, String owner) {
        if (type.equals("player")) {

            return owner.equals(player.getUniqueId().toString());

        } else if (type.equals("faction")) {

            return FactionAPI.getFaction(player).getName().equals(owner);

        }

        return false;
    }
}
