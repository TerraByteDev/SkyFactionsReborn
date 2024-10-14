package net.skullian.skyfactions.defence;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.DefenceHandler;
import net.skullian.skyfactions.util.hologram.TextHologram;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

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
    private BukkitTask task;
    private List<String> targetedEntities;

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
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getDISTANCE(), this.data.getLEVEL());
        return !solved.equals("N/A") ? Integer.parseInt(solved) : 0;
    }

    public int getDamage() {
        String solved = DefencesFactory.solveFormula(this.struct.getATTRIBUTES().getDAMAGE(), this.data.getLEVEL());
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

    public int getLevel() {
        return this.data.getLEVEL();
    }

    public CompletableFuture<List<LivingEntity>> getRandomEntity(World defenceWorld) {
        Location location = new Location(defenceWorld, data.getX(), data.getY(), data.getZ());
        int radius = getRadius();
        Collection<LivingEntity> nearbyEntities = defenceWorld.getNearbyLivingEntities(location, radius, radius, radius);
        List<LivingEntity> filteredEntities = new ArrayList<>();
        for (LivingEntity entity : nearbyEntities) {
            if (struct.getENTITY_CONFIG().isIS_WHITELIST() && struct.getENTITY_CONFIG().getENTITY_LIST().contains(entity.getType().name())) {
                filteredEntities.add(entity);
            } else if (!struct.getENTITY_CONFIG().isIS_WHITELIST() && !struct.getENTITY_CONFIG().getENTITY_LIST().contains(entity.getType().name())) {
                filteredEntities.add(entity);
            }
        }

        List<LivingEntity> chosenEntities = new ArrayList<>();
        if (!filteredEntities.isEmpty()) {
            for (int i = 0; i < getMaxSimEntities(); i++) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                chosenEntities.add(filteredEntities.get(random.nextInt(filteredEntities.size())));
            }
        }

        return CompletableFuture.completedFuture(chosenEntities);
    }

    public abstract void enable();

    public abstract void disable();

    public Entity getProjectileEntity() {
        return null; // todo
    }

    public boolean isEnabled() {
        return this.task != null;
    }

    public void createHologram(Location location, DefenceStruct defence, String playerName) {
        String text = String.join("\n", defence.getHOLOGRAM_LIST());

        TextHologram hologram = new TextHologram(playerName + "_" + defence.getFILE_NAME() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ())
                .setText(TextUtility.color(text.replace("%defence_name%", defence.getNAME())))
                .setSeeThroughBlocks(true)
                .setBillboard(Display.Billboard.VERTICAL)
                .setShadow(true)
                .setScale(1.0F, 1.0F, 1.0F);

        hologram.spawn(location.add(0, 1, 0));
        DefenceHandler.hologramsMap.put(hologram.getId(), hologram);
    }
}