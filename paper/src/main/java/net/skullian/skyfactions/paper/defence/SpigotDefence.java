package net.skullian.skyfactions.paper.defence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeff_media.customblockdata.CustomBlockData;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.defence.Defence;
import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceEntityStruct;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.SkyLocation;
import net.skullian.skyfactions.paper.PaperSharedConstants;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.defence.hologram.SpigotDefenceTextHologram;
import net.skullian.skyfactions.paper.hooks.CoreProtectHook;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SpigotDefence extends Defence {

    public SpigotDefence(DefenceData defenceData, DefenceStruct defenceStruct) {
        super(defenceData, defenceStruct);
    }

    @Override
    public CompletableFuture<Void> remove(SkyUser skyUser) {
        return CompletableFuture.runAsync(() -> {
            Block block = SpigotAdapter.adapt(getDefenceLocation()).getBlock();
            PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());

            Player player = SpigotAdapter.adapt(skyUser).getPlayer();
            if (player != null) CoreProtectHook.onDefenceBreak(player, block);

            container.remove(PaperSharedConstants.DEFENCE_DATA_KEY);
            block.setType(Material.AIR);

            if (getData().isIS_FACTION()) {
                Faction faction = SkyApi.getInstance().getFactionAPI().getFactionCache().get(getData().getUUIDFactionName());
                SkyApi.getInstance().getCacheService().getEntry(faction).removeDefence(getDefenceLocation());
            } else {
                SkyApi.getInstance().getCacheService().getEntry(UUID.fromString(getData().getUUIDFactionName()))
                        .removeDefence(getDefenceLocation());
            }
        });
    }

    @Override
    public void updatePDC() {
        DefenceData data = getData();
        try {
            PersistentDataContainer container = new CustomBlockData(SpigotAdapter.adapt(getDefenceLocation()).getBlock(), SkyFactionsReborn.getInstance());

            ObjectMapper mapper = new ObjectMapper();
            container.set(PaperSharedConstants.DEFENCE_DATA_KEY, PersistentDataType.STRING, mapper.writeValueAsString(data));

            SkyApi.getInstance().getDefenceAPI().getHologramsMap().get(getHologramID(data.getUUIDFactionName())).setData(data);
        } catch (JsonProcessingException exception) {
            SLogger.fatal("Error when trying to update PDC Defence Data for Defence [{}].", getHologramID(data.getUUIDFactionName()), exception);
        }
    }

    @Override
    public void applyPDC(Object entity) {
        Entity bukkitEntity = (Entity) entity;

        PersistentDataContainer container = bukkitEntity.getPersistentDataContainer();
        container.set(PaperSharedConstants.DEFENCE_DAMAGE_KEY, PersistentDataType.INTEGER, getDamage());

        if (bukkitEntity.getType().equals(EntityType.PLAYER)) {
            container.set(PaperSharedConstants.DEFENCE_DAMAGE_MESSAGE_KEY, PersistentDataType.STRING, getRandomActionMessage());
        }
    }

    @Override
    public List<Object> getRandomEntity(String defenceWorld) {
        if (isMaxEntitiesReached()) return getEntitiesFromUUIDs(defenceWorld);

        World world = Bukkit.getWorld(defenceWorld);
        if (world != null) {
            Location location = SpigotAdapter.adapt(getDefenceLocation());
            int radius = getRadius();

            Collection<LivingEntity> nearbyEntities = world.getNearbyLivingEntities(location, radius, radius, radius);
            if (nearbyEntities.isEmpty()) return new ArrayList<>();

            List<LivingEntity> filteredEntities = filterEntities(nearbyEntities);
            List<Object> chosenEntities = selectRandomEntities(filteredEntities);

            return chosenEntities;
        } else {
            SLogger.fatal("Could not find world [{}] for defence [{}].", defenceWorld, getStruct().getIDENTIFIER());
            return new ArrayList<>();
        }
    }

    private List<LivingEntity> filterEntities(Collection<LivingEntity> entities) {
        List<LivingEntity> filteredEntities = new ArrayList<>();
        List<String> allowedEntities = compileAllowedEntities();
        boolean shouldBlockNPCS = shouldBlockNPCs(allowedEntities);
        List<String> blockedMythicMobs = getBlockedMythicMobs(allowedEntities);

        getTargetedEntities().removeIf(currentlyTargeted -> entities.stream().noneMatch(entity -> entity.getUniqueId().equals(currentlyTargeted)));

        for (LivingEntity entity : entities) {
            if (isEntityAllowed(entity, allowedEntities, shouldBlockNPCS, blockedMythicMobs)) {
                filteredEntities.add(entity);
            }
        }

        return filteredEntities;
    }

    private boolean isEntityAllowed(LivingEntity entity, List<String> allowedEntities, boolean shouldBlockNPCS, List<String> blockedMythicMobs) {
        return entity.isVisibleByDefault() &&
                !entity.isInvisible() &&
                !(shouldBlockNPCS && entity.hasMetadata("NPC")) &&
                !(entity instanceof Player) && // TODO: check if raid is ongoing, if so target them
                !isBlockedMythicMob(entity, blockedMythicMobs) &&
                allowedEntities.contains(entity.getType().name());
    }

    private List<Object> selectRandomEntities(List<LivingEntity> entities) {
        List<Object> chosenEntities = new ArrayList<>();
        int maxEntities = getMaxSimEntities() - getTargetedEntities().size();

        for (int i = 0; i < maxEntities; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            chosenEntities.add(entities.get(random.nextInt(entities.size())));
        }

        return chosenEntities;
    }

    public List<String> compileAllowedEntities() {
        List<String> allowedEntities = new ArrayList<>();

        DefenceEntityStruct entityStruct = getStruct().getENTITY_CONFIG();
        if (canTargetPassive()) allowedEntities.addAll(entityStruct.getPASSIVE_LIST());
        if (canTargetHostile()) allowedEntities.addAll(entityStruct.getHOSTILE_LIST());
        if (entityStruct.isIS_WHITELIST()) allowedEntities.addAll(entityStruct.getENTITY_LIST());

        return allowedEntities;
    }

    public boolean shouldBlockNPCs(List<String> entities) {
        return entities.contains("NPC");
    }

    // acts as a boolean too
    public List<String> getBlockedMythicMobs(List<String> entities) {
        List<String> blockedMythicMobs = entities.stream()
                .filter(s -> s.startsWith("mythicmobs:"))
                .toList();
        if (DependencyHandler.isEnabled("MythicMobs")) return blockedMythicMobs;
            else if (!blockedMythicMobs.isEmpty()) SLogger.warn("Found {} blocked Mythic Mob IDs in defence [{}], but MythicMobs is not present / enabled in the integrations config!\nPlease see https://docs.terrabytedev.com/skyfactions/mechanics/defences/integrations.html#mythicmobs for more information.", getStruct().getIDENTIFIER());

        return null;
    }

    public boolean isBlockedMythicMob(LivingEntity entity, List<String> blockedIdentifiers) {
        AtomicBoolean isBlocked = new AtomicBoolean(false);
        try (MythicBukkit mythicBukkit = MythicBukkit.inst()) {
            mythicBukkit.getMobManager().getActiveMob(entity.getUniqueId()).ifPresent((mythicMob) -> {
                if (blockedIdentifiers.contains("mythicmobs:*")) isBlocked.set(true);
                if (blockedIdentifiers.contains(mythicMob.getMobType())) isBlocked.set(true);
            });
        }

        return isBlocked.get();
    }

    private List<Object> getEntitiesFromUUIDs(String defenceWorld) {
        World world = Bukkit.getWorld(defenceWorld);
        if (world != null) {
            return getTargetedEntities().stream()
                    .map(uuid -> (Object) world.getEntity(uuid))
                    .filter(entity -> entity != null)
                    .toList();
        } else {
            SLogger.fatal("Failed to find world [{}] for defence [{}].", defenceWorld, getStruct().getIDENTIFIER());
            return new ArrayList<>();
        }
    }

    @Override
    public void enable() {
        onEnable();
    }

    public abstract void onEnable();

    @Override
    public void createHologram(SkyLocation location, DefenceStruct defence, String playerUUIDorFactionName) {
        String text = String.join("\n", defence.getHOLOGRAM_LIST());

        DefenceTextHologram hologram = new SpigotDefenceTextHologram(playerUUIDorFactionName + "_" + defence.getIDENTIFIER() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ(), getData().getUUIDFactionName(), defence, getData())
                .setText(text.replace("defence_name", defence.getNAME()))
                .setSeeThroughBlocks(false)
                .setShadow(true);

        hologram.spawn(location.add(0.5, -0.2, 0.5));
        SkyApi.getInstance().getDefenceAPI().getHologramsMap().put(hologram.getId(), hologram);
    }
}
