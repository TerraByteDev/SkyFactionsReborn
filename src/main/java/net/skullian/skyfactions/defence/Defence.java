package net.skullian.skyfactions.defence;

import net.skullian.skyfactions.defence.struct.DefenceData;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.event.DefenceHandler;
import net.skullian.skyfactions.util.hologram.TextHologram;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;

public abstract class Defence {

    private DefenceData data;
    private DefenceStruct struct;
    private BukkitTask task;

    public Defence(DefenceData data) {
        this.data = data;
        if (struct == null)
            throw new IllegalArgumentException("Could not find defence with the type of " + data.getTYPE());
        this.struct = struct;
    }

    public DefenceData getData() {
        return this.data;
    }

    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public DefenceStruct getStruct() {
        return this.struct;
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

    public Collection<Entity> getNearbyEntities(World defenceWorld) {
        Location location = new Location(defenceWorld, data.getX(), data.getY(), data.getZ());
        int radius = getRadius();
        Collection<Entity> nearbyEntities = defenceWorld.getNearbyEntities(location, radius, radius, radius);
        return nearbyEntities; // todo
    }

    public abstract void enable();

    public abstract void disable();

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