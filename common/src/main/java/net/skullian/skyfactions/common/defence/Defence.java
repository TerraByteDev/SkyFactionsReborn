package net.skullian.skyfactions.common.defence;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import net.skullian.skyfactions.common.api.DefenceAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.defence.hologram.DefenceTextHologram;
import net.skullian.skyfactions.common.defence.struct.DefenceData;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.util.SkyLocation;

@Getter
@Setter
public abstract class Defence {

    private DefenceData data;
    private DefenceStruct struct;
    private ScheduledFuture<?> task;
    private List<UUID> targetedEntities = new ArrayList<>();
    private boolean noAmmoNotified;
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    public Defence(DefenceData defenceData, DefenceStruct defenceStruct) {
        this.data = defenceData;
        if (defenceStruct == null) {
            throw new NullPointerException("Could not find defence with the type of " + data.getTYPE());
        }
        this.struct = defenceStruct;
    }

    public abstract CompletableFuture<Void> remove();

    public void onShoot() {
        this.data.setAMMO(this.data.getAMMO() - 1);
        updatePDC();
    }

    public void damage(Optional<Integer> amount) {
        this.data.setDURABILITY(Math.max(this.data.getDURABILITY() - amount.orElseGet(this::getExplosionDamage), 0));
        updatePDC();
    }

    public abstract void updatePDC();

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
        String solved = SkyApi.getInstance().getDefenceAPI().solveFormula(formula, this.data.getLEVEL());
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

    public abstract void applyPDC(Object entity);

    public int getLevel() {
        return this.data.getLEVEL();
    }

    public abstract List<Object> getRandomEntity(String defenceWorld);

    public boolean isMaxEntitiesReached() {
        return targetedEntities.size() == getMaxSimEntities();
    }

    public boolean shouldBlockNPCs(List<String> entities) {
        return entities.contains("NPC");
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

    public void removeDeadEntity(int entityID) {
        targetedEntities.remove(entityID);
    }

    public void onLoad(String playerUUIDorFactionName) {
        if (!SkyApi.getInstance().getDefenceAPI().getHologramsMap().containsKey(getHologramID(playerUUIDorFactionName))) {
            createHologram(getDefenceLocation(), struct, playerUUIDorFactionName);
        }

        if (task != null) {
            return;
        }
        enable();
    }

    public abstract void enable();

    public void disable() {
        task.cancel(false);

        noAmmoNotified = false;
        targetedEntities.clear();

        String id = getHologramID(data.getUUIDFactionName());
        DefenceTextHologram holo = SkyApi.getInstance().getDefenceAPI().getHologramsMap().get(id);
        if (holo == null) {
            return;
        }
        holo.kill();
        SkyApi.getInstance().getDefenceAPI().getHologramsMap().remove(id);
    }

    public boolean isEnabled() {
        return this.task != null;
    }

    public SkyLocation getDefenceLocation() {
        return new SkyLocation(data.getWORLD_LOC(), data.getX(), data.getY(), data.getZ());
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

    public abstract void createHologram(SkyLocation location, DefenceStruct defence, String playerUUIDorFactionName);

    public void refresh() {
        DefenceStruct fetchedStruct = SkyApi.getInstance().getDefenceFactory().getDefences().get(this.data.getLOCALE()).get(struct.getIDENTIFIER());
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