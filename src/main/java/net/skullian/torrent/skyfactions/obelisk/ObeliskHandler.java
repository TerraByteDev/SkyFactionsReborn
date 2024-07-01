package net.skullian.torrent.skyfactions.obelisk;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.ObeliskConfig;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.island.PlayerIsland;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class ObeliskHandler {


    public static void spawnPlayerObelisk(Player player, PlayerIsland island) {
        World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
        if (world != null) {
            Location islandCenter = island.getCenter(world);
            List<Integer> offset = ObeliskConfig.OBELISK_SPAWN_OFFSET.getIntegerList();
            Location offsetLocation = islandCenter.add(offset.get(0), offset.get(1), offset.get(2));

            offsetLocation.getBlock().setType(Objects.requireNonNull(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString())));
            applyPDC(player.getUniqueId().toString(), offsetLocation.getBlock(), "player");

            if (ObeliskConfig.OBELISK_CUSTOM_MODEL_DATA.getInt() > 0) {
                SkyFactionsReborn.obeliskBlockManager.addBlock(offsetLocation, new ObeliskItem(new ItemStack(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString()))));
            }
        }
    }

    private static void applyPDC(String ownerIdentifier, Block block, String type) {
        BlockState blockState = block.getState();

        if (blockState instanceof TileState) {
            TileState tileState = (TileState) blockState;
            PersistentDataContainer container = tileState.getPersistentDataContainer();

            // Can either be 'player', or 'faction'.
            NamespacedKey obeliskKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
            container.set(obeliskKey, PersistentDataType.STRING, type);

            // This can either be a player UUID (as string), or faction name. Depends on the obelisk_type
            NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
            container.set(ownerKey, PersistentDataType.STRING, ownerIdentifier);

            tileState.update();
        }
    }
}
