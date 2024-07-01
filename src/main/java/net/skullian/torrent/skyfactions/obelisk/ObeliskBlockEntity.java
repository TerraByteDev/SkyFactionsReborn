package net.skullian.torrent.skyfactions.obelisk;

import lombok.Getter;
import net.skullian.torrent.skyfactions.config.ObeliskConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

@Getter
public class ObeliskBlockEntity {
    private Material BROKEN_MATERIAL = Material.AIR;

    private Location location;
    private ItemDisplay entity;
    private ObeliskItem blockItem;

    public ObeliskBlockEntity(Location location, ObeliskItem blockItem) {
        this.placeBlock(location, blockItem);
    }

    private void placeBlock(Location location, ObeliskItem blockItem) {
        World world = location.getWorld();

        Location entityLocation = getLocationFromBlock(location);

        world.spawn(entityLocation, ItemDisplay.class, entity -> {
            ItemStack item = blockItem.getItem(1);
            entity.setItemStack(item);
            entity.setPersistent(true);
            entity.setInvisible(true);

            this.entity = entity;
            this.location = location;
            this.blockItem = blockItem;
        });
    }

    public void breakBlock() {
        this.entity.remove();
        World world = this.location.getWorld();
        world.setBlockData(this.location, BROKEN_MATERIAL.createBlockData());
    }

    private Location getLocationFromBlock (Location location) {
        Location entityLocation = location.clone();
        entityLocation.setX( location.getX() + 0.5 );
        entityLocation.setY( location.getY() + 0.5 );
        entityLocation.setZ( location.getZ() + 0.5 );

        return entityLocation;
    }
}
