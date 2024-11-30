package net.skullian.skyfactions.paper.obelisk;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.obelisk.ObeliskBlockEntity;
import net.skullian.skyfactions.common.obelisk.ObeliskItem;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@Getter
@Setter
public class SpigotObeliskBlockEntity extends ObeliskBlockEntity {

    private ItemDisplay entity;

    @Override
    public void placeBlock(SkyLocation location, ObeliskItem blockItem) {
        Location sLocation = SpigotAdapter.adapt(location);
        World world = sLocation.getWorld();

        world.setBlockData(sLocation, Material.valueOf(getHITBOX_MATERIAL()).createBlockData());
        Location entityLocation = SpigotAdapter.adapt(getLocationFromBlock(location));

        world.spawn(entityLocation, ItemDisplay.class, entity -> {
            ItemStack itemStack = SpigotAdapter.adapt(blockItem.getItem(1), null);
            entity.setItemStack(itemStack);
            entity.setPersistent(true);
            entity.setInvisible(true);

            NamespacedKey key = new NamespacedKey(SkyFactionsReborn.getInstance(), "obelisk");
            entity.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);

            setEntity(entity);
            setLocation(location);
            setBlockItem(blockItem);
        });
    }

    @Override
    public void breakBlock() {
        Location location = SpigotAdapter.adapt(getLocation());

        getEntity().remove();
        World world = location.getWorld();
        world.setBlockData(location, Material.valueOf(getBROKEN_MATERIAL()).createBlockData());
    }
}
