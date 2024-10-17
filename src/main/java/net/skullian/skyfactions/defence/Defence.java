package net.skullian.skyfactions.defence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.DefenceHandler;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public abstract class Defence {

    private DefenceData data;
    private DefenceStruct struct;
    private int task = -1;
    private List<LivingEntity> targetedEntities = new ArrayList<>();

    public Defence(DefenceData defenceData, DefenceStruct defenceStruct) {
        this.data = defenceData;
        if (defenceStruct == null)
            throw new NullPointerException("Could not find defence with the type of " + data.getTYPE());
        this.struct = defenceStruct;
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
        List<LivingEntity> filteredEntities = new ArrayList<>();
        targetedEntities.removeIf(currentlyTargeted -> !nearbyEntities.contains(currentlyTargeted));

        for (LivingEntity entity : nearbyEntities) {
            if (entity.isInvisible()) continue;
            if (entity instanceof Player) continue; // TODO: check if raid is ongoing, if so target them

            if (struct.getENTITY_CONFIG().isIS_WHITELIST() && struct.getENTITY_CONFIG().getENTITY_LIST().contains(entity.getType().name()) && !filteredEntities.contains(entity)) {
                filteredEntities.add(entity);
            } else if (!struct.getENTITY_CONFIG().isIS_WHITELIST() && !struct.getENTITY_CONFIG().getENTITY_LIST().contains(entity.getType().name()) && !filteredEntities.contains(entity)) {
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
        );
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