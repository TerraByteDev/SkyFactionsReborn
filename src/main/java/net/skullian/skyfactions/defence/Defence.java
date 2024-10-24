package net.skullian.skyfactions.defence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeff_media.customblockdata.CustomBlockData;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceEntityStruct;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.DefenceHandler;
import net.skullian.skyfactions.util.DependencyHandler;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.hologram.TextHologram;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
        if (defenceStruct == null)
            throw new NullPointerException("Could not find defence with the type of " + data.getTYPE());
        this.struct = defenceStruct;
    }

    public void onShoot() {
        this.data.setAMMO(this.data.getAMMO() - 1);
        updatePDC();
    }

    public void damage(Optional<Integer> amount) {
        this.data.setDURABILITY((Math.max(this.data.getDURABILITY() - (amount.orElseGet(this::getExplosionDamage)), 0)));
        updatePDC();
    }

    public void updatePDC() {
        try {
            PersistentDataContainer container = new CustomBlockData(getDefenceLocation().getBlock(), SkyFactionsReborn.getInstance());
            NamespacedKey dataKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-data");

            ObjectMapper mapper = new ObjectMapper();
            container.set(dataKey, PersistentDataType.STRING, mapper.writeValueAsString(data));
        } catch (JsonProcessingException exception) {
            SLogger.fatal("Error when trying to update PDC Defence Data for Defence [{}].", getHologramID(data.getUUIDFactionName()), exception);
        }
    }

    public String getType() {
        return this.struct.getTYPE();
    }

    public int getRadius() {
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getRANGE(), this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public int getDamage() {
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getDAMAGE(), this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public int getDistance() { // for springs
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getDISTANCE(), this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public int getHealing() { // for healing defences
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getHEALING(), this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public int getMaxSimEntities() {
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getMAX_TARGETS(), this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public int getRate() {
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getCOOLDOWN(), this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public int getExplosionDamage() {
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getEXPLOSION_DAMAGE_PERCENT(), this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public String getRandomActionMessage() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<String> messages = this.struct.getEFFECT_MESSAGES();
        return messages.get(random.nextInt(messages.size())).replaceAll("%defence_name%", this.struct.getNAME())
                .replaceAll("%damage%", String.valueOf(getDamage()));
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
        if (getData().getDURABILITY() < 100) {

            if (DefenceHandler.hologramsMap.containsKey(getHologramID(data.getUUIDFactionName()))) {
                TextHologram holo = DefenceHandler.hologramsMap.get(getHologramID(data.getUUIDFactionName()));
                holo.setText(
                        struct.isAPPEND_DURABILITY_AT_TOP() ? TextUtility.color(struct.getDURABILITY_HOLOGRAM().replace("%durability%", String.valueOf(data.getDURABILITY())) + "\n" + holo.getText())
                                : TextUtility.color(holo.getText() + "\n" + struct.getDURABILITY_HOLOGRAM().replace("%durability%", String.valueOf(data.getDURABILITY())))
                );
                holo.update();
            }
        }

        return getData().getDURABILITY() != 0;
    }

    public boolean checkAmmo() {
        if (getData().getAMMO() == 0) {
            if (this.noAmmoNotified) return false;

            if (DefenceHandler.hologramsMap.containsKey(getHologramID(data.getUUIDFactionName()))) {
                TextHologram holo = DefenceHandler.hologramsMap.get(getHologramID(data.getUUIDFactionName()));
                holo.setText(
                        struct.isAPPEND_OUT_OF_STOCK_TO_TOP() ? TextUtility.color(struct.getOUT_OF_STOCK_HOLOGRAM() + "\n" + holo.getText()) : TextUtility.color(holo.getText() + "\n" + struct.getOUT_OF_STOCK_HOLOGRAM())
                );
                holo.update();
                this.noAmmoNotified = true;
            }

            return false;
        }

        return true;
    }

    public void applyPDC(Entity entity) {
        NamespacedKey damageKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-damage");

        PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(damageKey, PersistentDataType.INTEGER, getDamage());

        if (entity.getType().equals(EntityType.PLAYER)) {
            // better than the eventhandler searching itself, store the ranadom damage / action msg here.
            NamespacedKey messageKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "damage-message");

            container.set(messageKey, PersistentDataType.STRING, getRandomActionMessage());
        }
    }

    public int getLevel() {
        return this.data.getLEVEL();
    }

    public CompletableFuture<List<LivingEntity>> getRandomEntity(World defenceWorld) {
        if (targetedEntities.size() == getMaxSimEntities()) return CompletableFuture.completedFuture(targetedEntities);

        Location location = new Location(defenceWorld, data.getX(), data.getY(), data.getZ());
        int radius = getRadius();

        Collection<LivingEntity> nearbyEntities = defenceWorld.getNearbyLivingEntities(location, radius, radius, radius);
        if (nearbyEntities.isEmpty()) return CompletableFuture.completedFuture(new ArrayList<LivingEntity>());

        List<LivingEntity> filteredEntities = new ArrayList<>();
        targetedEntities.removeIf(currentlyTargeted -> !nearbyEntities.contains(currentlyTargeted));

        List<String> allowedEntities = compileAllowedEntities();
        boolean shouldBlockNPCS = shouldBlockNPCs(allowedEntities);
        List<String> blockedMythicMobs = getBlockedMythicMobs(allowedEntities);
        for (LivingEntity entity : nearbyEntities) {
            if (!entity.isVisibleByDefault()) continue;
            if (entity.isInvisible()) continue;
            if (shouldBlockNPCS && entity.hasMetadata("NPC")) continue;
            if (entity instanceof Player) continue; // TODO: check if raid is ongoing, if so target them
            if (blockedMythicMobs != null && isBlockedMythicMob(entity, blockedMythicMobs)) continue;

            if (allowedEntities.contains(entity.getType().name()) && !filteredEntities.contains(entity)) {
                filteredEntities.add(entity);
            }
        }

        List<LivingEntity> chosenEntities = new ArrayList<>();
        if (!filteredEntities.isEmpty()) {
            for (int i = 0; i < (getMaxSimEntities() - targetedEntities.size()); i++) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                chosenEntities.add(filteredEntities.get(random.nextInt(filteredEntities.size())));
            }
        }

        chosenEntities.addAll(targetedEntities);
        return CompletableFuture.completedFuture(chosenEntities);
    }

    public List<String> compileAllowedEntities() {
        List<String> allowedEntities = new ArrayList<>();

        DefenceEntityStruct entityStruct = struct.getENTITY_CONFIG();
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
        if (DependencyHandler.enabledDeps.contains("MythicMobs")) {
            return entities.stream()
                    .filter(s -> s.startsWith("mythicmobs:"))
                    .collect(Collectors.toList());
        }

        return null;
    }

    public boolean isBlockedMythicMob(LivingEntity entity, List<String> blockedIdentifiers) {
        AtomicBoolean isblocked = new AtomicBoolean(false);
        MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).ifPresent(mm -> {
            if (blockedIdentifiers.contains("mythicmobs:*")) isblocked.set(true);
            if (blockedIdentifiers.contains(mm.getMobType())) isblocked.set(true);
        });

        return isblocked.get();
    }

    public boolean canTargetPassive() {
        return struct.getENTITY_CONFIG().isALLOW_PASSIVE_TARGETING() && data.getLEVEL() >= struct.getATTRIBUTES().getPASSIVE_MOBS_TARGET_LEVEL() && data.isTARGET_PASSIVE();
    }

    public boolean canTargetHostile() {
        return struct.getENTITY_CONFIG().isALLOW_HOSTILE_TARGETING() && data.getLEVEL() >= struct.getATTRIBUTES().getHOSTILE_MOBS_TARGET_LEVEL() && data.isTARGET_PASSIVE();
    }

    public void removeDeadEntity(LivingEntity entity) {
        targetedEntities.remove(entity);
    }

    public void onLoad(String playerUUIDorFactionName) {
        if (!DefenceHandler.hologramsMap.containsKey(getHologramID(playerUUIDorFactionName))) {
            createHologram(getDefenceLocation(), struct, playerUUIDorFactionName);
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
        TextHologram holo = DefenceHandler.hologramsMap.get(id);
        if (holo == null) return;
        holo.kill();
        DefenceHandler.hologramsMap.remove(id);
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

        TextHologram hologram = new TextHologram(playerUUIDorFactionName + "_" + defence.getIDENTIFIER() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ(), TextHologram.RenderMode.ALL, data.getUUIDFactionName())
                .setText(TextUtility.color(text.replace("%defence_name%", defence.getNAME())))
                .setBillboard(Display.Billboard.VERTICAL)
                .setSeeThroughBlocks(false)
                .setShadow(true)
                .setScale(1.0F, 1.0F, 1.0F);

        hologram.spawn(location.add(0.5, -0.2, 0.5));
        DefenceHandler.hologramsMap.put(hologram.getId(), hologram);
    }

    public void refresh() {
        DefenceStruct fetchedStruct = DefencesFactory.defences.get(struct.getIDENTIFIER());
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
        ); // yes this is hardcoded

        return allowed.contains(configuredProjectile);
    }
}
