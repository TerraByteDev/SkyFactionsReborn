package net.skullian.torrent.skyfactions.obelisk;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ObeliskBlockManager {

    private HashMap<String, ObeliskBlockEntity> blocks;

    public ObeliskBlockManager() {
        blocks = new HashMap<>();
        this.preLoad();
    }

    public void addBlock(Location location, ObeliskItem item) {
        String blockID = locToString(location);
        ObeliskBlockEntity blockEntity = new ObeliskBlockEntity(location, item);

        blocks.put(blockID, blockEntity);
    }

    private String locToString(Location location) {
        return location.getWorld().getName() + "_z" + location.getBlockX() + "y" + location.getBlockY() + "z" + location.getBlockZ();
    }

    private Location getLocationFromEntity (Location location) {
        Location entityLocation = location.clone();
        entityLocation.setX( location.getX() - 0.5 );
        entityLocation.setY( location.getY() - 0.5 );
        entityLocation.setZ( location.getZ() - 0.5 );

        return entityLocation;
    }

    private void preLoad() {
        for (World world : SkyFactionsReborn.getInstance().getServer().getWorlds()) {
            for (ItemDisplay entity : world.getEntitiesByClass(ItemDisplay.class)) {
                Location location = getLocationFromEntity(entity.getLocation());
                ItemStack item = entity.getItemStack();
                ObeliskItem obeliskItem = new ObeliskItem(item);
                entity.remove();
                this.addBlock(location, obeliskItem);
            }
        }
    }
}
