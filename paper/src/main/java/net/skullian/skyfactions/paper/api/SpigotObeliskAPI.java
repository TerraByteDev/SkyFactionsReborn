package net.skullian.skyfactions.paper.api;

import com.github.puregero.multilib.MultiLib;
import com.jeff_media.customblockdata.CustomBlockData;
import lombok.Getter;
import net.skullian.skyfactions.common.api.ObeliskAPI;
import net.skullian.skyfactions.common.config.types.ObeliskConfig;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.island.SkyIsland;
import net.skullian.skyfactions.common.obelisk.ObeliskBlockEntity;
import net.skullian.skyfactions.common.obelisk.ObeliskItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.PaperSharedConstants;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.obelisk.SpigotObeliskBlockEntity;
import org.bukkit.*;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter
public class SpigotObeliskAPI extends ObeliskAPI {

    private final Map<SkyLocation, SpigotObeliskBlockEntity> blockEntities = new HashMap<>();

    @Override
    public void spawnPlayerObelisk(SkyUser player, SkyIsland island) {
        MultiLib.getGlobalRegionScheduler().run(SkyFactionsReborn.getInstance(), (consumer) -> {
            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world != null) {
                SkyLocation center = island.getCenter(world.getName());
                List<Integer> offset = ObeliskConfig.OBELISK_SPAWN_OFFSET.getIntegerList();

                SkyLocation offsetLocation = center.add(offset.get(0), offset.get(1), offset.get(2));

                if (ObeliskConfig.OBELISK_CUSTOM_MODEL_DATA.getInt() != -1) {
                    SpigotObeliskBlockEntity bEntity = new SpigotObeliskBlockEntity();
                    bEntity.placeBlock(offsetLocation, new ObeliskItem(SkyItemStack.builder().material(ObeliskConfig.OBELISK_MATERIAL.getString()).build()));

                    blockEntities.put(offsetLocation, bEntity);
                } else {
                    Material obeliskMaterial = Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString());
                    if (obeliskMaterial == null) return;
                    SpigotAdapter.adapt(offsetLocation).getBlock().setType(obeliskMaterial);
                }

                applyPersistentData(player.getUniqueId().toString(), offsetLocation, "player");
            }
        });
    }

    @Override
    public void spawnFactionObelisk(String faction, SkyIsland island) {
        MultiLib.getGlobalRegionScheduler().run(SkyFactionsReborn.getInstance(), (consumer) -> {
            World world = Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString());
            if (world != null) {
                SkyLocation center = island.getCenter(world.getName());
                List<Integer> offset = ObeliskConfig.OBELISK_SPAWN_OFFSET.getIntegerList();

                SkyLocation offsetLocation = center.add(offset.get(0), offset.get(1), offset.get(2));

                if (ObeliskConfig.OBELISK_CUSTOM_MODEL_DATA.getInt() != -1) {
                    SpigotObeliskBlockEntity bEntity = new SpigotObeliskBlockEntity();
                    bEntity.placeBlock(offsetLocation, new ObeliskItem(SkyItemStack.builder().material(ObeliskConfig.OBELISK_MATERIAL.getString()).build()));

                    blockEntities.put(offsetLocation, bEntity);
                } else {
                    Material obeliskMaterial = Material.getMaterial(ObeliskConfig.OBELISK_MATERIAL.getString());
                    if (obeliskMaterial == null) return;
                    SpigotAdapter.adapt(offsetLocation).getBlock().setType(obeliskMaterial);
                }

                applyPersistentData(faction, offsetLocation, "faction");
            }
        });
    }

    @Override
    public void applyPersistentData(String ownerIdentifier, SkyLocation location, String type) {
        Location sLocation = SpigotAdapter.adapt(location);
        PersistentDataContainer container = new CustomBlockData(sLocation.getBlock(), SkyFactionsReborn.getInstance());
        container.set(PaperSharedConstants.OBELISK_TYPE_KEY, PersistentDataType.STRING, type);

        // This can either be a player UUID (as string), or faction name. Depends on the obelisk-type
        container.set(PaperSharedConstants.OBELISK_OWNER_KEY, PersistentDataType.STRING, ownerIdentifier);
    }

    @Override
    public void onIslandDelete(SkyIsland island, String type) {
        World world = Bukkit.getWorld(type.equalsIgnoreCase("player") ? Settings.ISLAND_PLAYER_WORLD.getString() : Settings.ISLAND_FACTION_WORLD.getString());
        if (world != null) {
            SkyLocation center = island.getCenter(world.getName());
            List<Integer> offset = ObeliskConfig.OBELISK_SPAWN_OFFSET.getIntegerList();

            SkyLocation offsetLocation = center.add(offset.get(0), offset.get(1), offset.get(2));
            ObeliskBlockEntity bEntity = blockEntities.get(offsetLocation);

            if (bEntity != null) {
                bEntity.breakBlock();
                blockEntities.remove(offsetLocation);
            }
        }
    }

    private Location getLocationFromEntity(Location location) {
        Location entityLocation = location.clone();
        entityLocation.setX( location.getX() - 0.5 );
        entityLocation.setY( location.getY() - 0.5 );
        entityLocation.setZ( location.getZ() - 0.5 );

        return entityLocation;
    }

    @Override
    public void preLoad() {

        List<World> worlds = new ArrayList<>();
        worlds.add(Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString()));
        worlds.add(Bukkit.getWorld(Settings.ISLAND_FACTION_WORLD.getString()));

        for (World world : worlds) {
            for (ItemDisplay entity : world.getEntitiesByClass(ItemDisplay.class)) {
                if (!entity.getPersistentDataContainer().has(PaperSharedConstants.OBELISK_KEY) || MultiLib.isChunkExternal(entity)) continue;

                Location location = getLocationFromEntity(entity.getLocation());
                SpigotObeliskBlockEntity blockEntity = new SpigotObeliskBlockEntity();
                blockEntity.setEntity(entity);

                blockEntities.put(SpigotAdapter.adapt(location), blockEntity);
            }
        }
    }
}
