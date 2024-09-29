package net.skullian.torrent.skyfactions.obelisk;

import com.jeff_media.customblockdata.CustomBlockData;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.types.ObeliskConfig;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.faction.Faction;
import net.skullian.torrent.skyfactions.island.FactionIsland;
import net.skullian.torrent.skyfactions.island.PlayerIsland;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ObeliskHandler {


    public static void spawnPlayerObelisk(Player player, PlayerIsland island) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world != null) {
                Location islandCenter = island.getCenter(world);
                List<Integer> offset = ObeliskConfig.OBELISK_SPAWN_OFFSET.getIntegerList();
                Location offsetLocation = islandCenter.add(offset.get(0), offset.get(1), offset.get(2));

                if (ObeliskConfig.OBELISK_CUSTOM_MODEL_DATA.getInt() > 0) {
                    new ObeliskBlockEntity(offsetLocation, new ObeliskItem(new ItemStack(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString()))));
                } else {
                    offsetLocation.getBlock().setType(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString()));
                }

                applyPDC(player.getUniqueId().toString(), offsetLocation.getBlock(), "player");
            }
        });
    }

    public static void spawnFactionObelisk(Faction faction, FactionIsland island) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
            if (world != null) {
                Location islandCenter = island.getCenter(world);
                List<Integer> offset = ObeliskConfig.OBELISK_SPAWN_OFFSET.getIntegerList();
                Location offsetLocation = islandCenter.add(offset.get(0), offset.get(1), offset.get(2));

                if (ObeliskConfig.OBELISK_CUSTOM_MODEL_DATA.getInt() > 0) {
                    new ObeliskBlockEntity(offsetLocation, new ObeliskItem(new ItemStack(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString()))));
                } else {
                    offsetLocation.getBlock().setType(Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString()));
                }

                applyPDC(faction.getName(), offsetLocation.getBlock(), "faction");
            }
        });
    }

    private static void applyPDC(String ownerIdentifier, Block block, String type) {

        PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
        NamespacedKey obeliskKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_type");
        container.set(obeliskKey, PersistentDataType.STRING, type);

        // This can either be a player UUID (as string), or faction name. Depends on the obelisk_type
        NamespacedKey ownerKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk_owner");
        container.set(ownerKey, PersistentDataType.STRING, ownerIdentifier);

    }
}
