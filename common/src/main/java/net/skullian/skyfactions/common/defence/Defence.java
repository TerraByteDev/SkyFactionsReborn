package net.skullian.skyfactions.common.defence;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import net.skullian.skyfactions.core.api.FactionAPI;
import net.skullian.skyfactions.core.faction.Faction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeff_media.customblockdata.CustomBlockData;

import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceEntityStruct;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.core.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.common.util.DependencyHandler;
import net.skullian.skyfactions.core.util.SLogger;
import net.skullian.skyfactions.core.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.util.text.TextUtility;

@Getter
@Setter
public abstract class Defence {

    private DefenceData data;
    private DefenceStruct struct;
    private int task = -1;
    private List<LivingEntity> targetedEntities = new ArrayList<>();
    private boolean noAmmoNotified;

    public Defence(DefenceData defenceData, DefenceStruct defenceStruct) {
        this.data = defenceData;
        if (defenceStruct == null) {
            throw new NullPointerException("Could not find defence with the type of " + data.getTYPE());
        }
        this.struct = defenceStruct;
    }

    public CompletableFuture<Void> remove() {
        return CompletableFuture.runAsync(() -> {
            Block block = getDefenceLocation().getBlock();
            PersistentDataContainer container = new CustomBlockData(block, SkyFactionsReborn.getInstance());
            NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");

            container.remove(dataKey);
            block.setType(Material.AIR);

            if (data.isIS_FACTION()) {
                Faction faction = FactionAPI.factionNameCache.get(data.getUUIDFactionName());
                SkyFactionsReborn.getCacheService().getEntry(faction).removeDefence(getDefenceLocation());
            } else {
                SkyFactionsReborn.getCacheService().getEntry(UUID.fromString(data.getUUIDFactionName())).removeDefence(getDefenceLocation());
            }
        });
    }

    public void onShoot() {
        this.data.setAMMO(this.data.getAMMO() - 1);
        updatePDC();
    }

    public void damage(Optional<Integer> amount) {
        this.data.setDURABILITY(Math.max(this.data.getDURABILITY() - amount.orElseGet(this::getExplosionDamage), 0));
        updatePDC();
    }

    public void updatePDC() {
        try {
            PersistentDataContainer container = new CustomBlockData(getDefenceLocation().getBlock(), SkyFactionsReborn.getInstance());
            NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");

            ObjectMapper mapper = new ObjectMapper();
            container.set(dataKey, PersistentDataType.STRING, mapper.writeValueAsString(data));

            DefencePlacementHandler.hologramsMap.get(getHologramID(data.getUUIDFactionName())).setData(data);
        } catch (JsonProcessingException exception) {
            SLogger.fatal("Error when trying to update PDC Defence Data for Defence [{}].", getHologramID(data.getUUIDFactionName()), exception);
        }
    }

    public String getType() {
        return this.struct.getTYPE();
    }

    public int getRadius() {
        return parseFormula(this.struct.getATTRIBUTES().getRANGE());
    }

    public int getDamage() {
        return parseFormula(this.struct.getATTRIBUTES().getDAMAGE());
    }

    public int getDistance() {
        return parseFormula(this.struct.getATTRIBUTES().getDISTANCE());
    }

    public int getHealing() {
        return parseFormula(this.struct.getATTRIBUTES().getHEALING());
    }

    public int getMaxSimEntities() {
        return parseFormula(this.struct.getATTRIBUTES().getMAX_TARGETS());
    }

    public int getRate() {
        return parseFormula(this.struct.getATTRIBUTES().getCOOLDOWN());
    }

    public int getExplosionDamage() {
        return parseFormula(this.struct.getATTRIBUTES().getEXPLOSION_DAMAGE_PERCENT());
    }

    private int parseFormula(String formula) {
        String solved = DefencesFactory.solveFormula(formula, this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public String getRandomActionMessage() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<String> messages = this.struct.getEFFECT_MESSAGES();
        return messages.get(random.nextInt(messages.size()))
                .replaceAll("defence_name", this.struct.getNAME())
                .replaceAll("damage", String.valueOf(getDamage()));
    }

    public String getRandomDeathMessage() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<String> messages = this.struct.getDEATH_MESSAGES();
        return messages.get(random.nextInt(messages.size()));
    }

    public boolean canShoot() {
        return checkAmmo() && checkDurability();
    }

    public boolean checkDurability() {
        return getData().getDURABILITY() != 0;
    }

    public boolean checkAmmo() {
        return getData().getAMMO() != 0 && !this.noAmmoNotified;
    }

    public void applyPDC(Entity entity) {
        NamespacedKey damageKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-damage");

        PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(damageKey, PersistentDataType.INTEGER, getDamage());

        if (entity.getType().equals(EntityType.PLAYER)) {
            NamespacedKey messageKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "damage-message");
            container.set(messageKey, PersistentDataType.STRING, getRandomActionMessage());
        }
    }

    public int getLevel() {
        return this.data.getLEVEL();
    }

    public CompletableFuture<List<LivingEntity>> getRandomEntity(World defenceWorld) {
        if (isMaxEntitiesReached()) {
            return CompletableFuture.completedFuture(targetedEntities);
        }

        Location location = getDefenceLocation();
        int radius = getRadius();

        List<LivingEntity> filteredEntities = defenceWorld.getNearbyLivingEntities(location, radius, radius, radius).stream()
                .filter(this::isEntityAllowed)
                .collect(Collectors.toList());

        List<LivingEntity> chosenEntities = ThreadLocalRandom.current()
                .ints(0, filteredEntities.size())
                .distinct()
                .limit(getMaxSimEntities() - targetedEntities.size())
                .mapToObj(filteredEntities::get)
                .collect(Collectors.toList());

        chosenEntities.addAll(targetedEntities);
        return CompletableFuture.completedFuture(chosenEntities);
    }

    private boolean isMaxEntitiesReached() {
        return targetedEntities.size() == getMaxSimEntities();
    }

    private boolean isEntityAllowed(LivingEntity entity) {
        List<String> allowedEntities = compileAllowedEntities();
        boolean shouldBlockNPCS = shouldBlockNPCs(allowedEntities);
        List<String> blockedMythicMobs = getBlockedMythicMobs(allowedEntities);

        return entity.isVisibleByDefault() &&
                !entity.isInvisible() &&
                !(shouldBlockNPCS && entity.hasMetadata("NPC")) &&
                !(entity instanceof Player) &&
                !isBlockedMythicMob(entity, blockedMythicMobs) &&
                allowedEntities.contains(entity.getType().name());
    }

    private List<LivingEntity> selectRandomEntities(List<LivingEntity> entities) {
        List<LivingEntity> chosenEntities = new ArrayList<>();
        int maxEntities = getMaxSimEntities() - targetedEntities.size();

        for (int i = 0; i < maxEntities; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            chosenEntities.add(entities.get(random.nextInt(entities.size())));
        }

        return chosenEntities;
    }

    public List<String> compileAllowedEntities() {
        List<String> allowedEntities = new ArrayList<>();

        DefenceEntityStruct entityStruct = struct.getENTITY_CONFIG();
        if (canTargetPassive()) {
            allowedEntities.addAll(entityStruct.getPASSIVE_LIST());
        }
        if (canTargetHostile()) {
            allowedEntities.addAll(entityStruct.getHOSTILE_LIST());
        }
        if (entityStruct.isIS_WHITELIST()) {
            allowedEntities.addAll(entityStruct.getENTITY_LIST());
        }

        return allowedEntities;
    }

    public boolean shouldBlockNPCs(List<String> entities) {
        return entities.contains("NPC");
    }

    public List<String> getBlockedMythicMobs(List<String> entities) {
        if (DependencyHandler.enabledDeps.contains("MythicMobs")) {
            return entities.stream()
                    .filter(s -> s.startsWith("mythicmobs:"))
                    .collect(Collectors.toList());
        }

        return null;
    }

    public boolean isBlockedMythicMob(LivingEntity entity, List<String> blockedIdentifiers) {
        AtomicBoolean isBlocked = new AtomicBoolean(false);
        MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).ifPresent(mm -> {
            if (blockedIdentifiers.contains("mythicmobs:*")) {
                isBlocked.set(true);
            }
            if (blockedIdentifiers.contains(mm.getMobType())) {
                isBlocked.set(true);
            }
        });

        return isBlocked.get();
    }

    public boolean canTargetPassive() {
        return struct.getENTITY_CONFIG().isALLOW_PASSIVE_TARGETING() &&
                data.getLEVEL() >= struct.getATTRIBUTES().getPASSIVE_MOBS_TARGET_LEVEL() &&
                data.isTARGET_PASSIVE();
    }

    public boolean canTargetHostile() {
        return struct.getENTITY_CONFIG().isALLOW_HOSTILE_TARGETING() &&
                data.getLEVEL() >= struct.getATTRIBUTES().getHOSTILE_MOBS_TARGET_LEVEL() &&
                data.isTARGET_PASSIVE();
    }

    public void removeDeadEntity(LivingEntity entity) {
        targetedEntities.remove(entity);
    }

    public void onLoad(String playerUUIDorFactionName) {
        if (!DefencePlacementHandler.hologramsMap.containsKey(getHologramID(playerUUIDorFactionName))) {
            createHologram(getDefenceLocation(), struct, playerUUIDorFactionName);
        }

        if (task != -1) {
            return;
        }
        enable();
    }

    public abstract void enable();

    public void disable() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;

        noAmmoNotified = false;
        targetedEntities.clear();

        String id = getHologramID(data.getUUIDFactionName());
        DefenceTextHologram holo = DefencePlacementHandler.hologramsMap.get(id);
        if (holo == null) {
            return;
        }
        holo.kill();
        DefencePlacementHandler.hologramsMap.remove(id);
    }

    public boolean isEnabled() {
        return this.task != -1;
    }

    public Location getDefenceLocation() {
        World world = Bukkit.getWorld(data.getWORLD_LOC());
        return new Location(world, data.getX(), data.getY(), data.getZ());
    }

    public String getHologramID(String playerUUIDorFactionName) {
        return String.format("%s_%s_%s_%s_%s",
                playerUUIDorFactionName,
                struct.getIDENTIFIER(),
                data.getX(),
                data.getY(),
                data.getZ()
        ).toLowerCase();
    }

    public void createHologram(Location location, DefenceStruct defence, String playerUUIDorFactionName) {
        String text = String.join("\n", defence.getHOLOGRAM_LIST());

        DefenceTextHologram hologram = new DefenceTextHologram(playerUUIDorFactionName + "_" + defence.getIDENTIFIER() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ(), DefenceTextHologram.RenderMode.ALL, data.getUUIDFactionName(), defence, data)
                .setText(TextUtility.color(text.replace("defence_name", defence.getNAME()), Messages.getDefaulLocale(), null))
                .setBillboard(Display.Billboard.VERTICAL)
                .setSeeThroughBlocks(false)
                .setShadow(true)
                .setScale(1.0F, 1.0F, 1.0F);

        hologram.spawn(location.add(0.5, -0.2, 0.5));
        DefencePlacementHandler.hologramsMap.put(hologram.getId(), hologram);
    }

    public void refresh() {
        DefenceStruct fetchedStruct = DefencesFactory.defences.get(this.data.getLOCALE()).get(struct.getIDENTIFIER());
        if (fetchedStruct != null) {
            this.struct = fetchedStruct;
        }
    }

    public boolean isAllowed(String configuredProjectile) {
        List<String> allowed = List.of(
                "ARROW",
                "WIND_CHARGE",
                "DRAGON_FIREBALL",
                "EGG",
                "ENDER_PEARL",
                "FIREBALL",
                "FIREWORK",
                "FISHING_BOBBER",
                "POTION",
                "LLAMA_SPIT",
                "SHULKER_BULLET",
                "SMALL_FIREBALL",
                "SNOWBALL",
                "SPECTRAL_ARROW",
                "EXPERIENCE_BOTTLE",
                "TRIDENT",
                "WIND_CHARGE",
                "WITHER_SKULL"
        );

        return allowed.contains(configuredProjectile);
    }
}