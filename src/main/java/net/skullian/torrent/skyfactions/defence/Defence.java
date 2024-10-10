package net.skullian.torrent.skyfactions.defence;

import net.skullian.torrent.skyfactions.defence.struct.DefenceData;
import net.skullian.torrent.skyfactions.defence.struct.DefenceStruct;
import net.skullian.torrent.skyfactions.event.DefenceHandler;
import net.skullian.torrent.skyfactions.util.hologram.TextHologram;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;

public abstract class Defence {

    private DefenceData data;
    private BukkitTask task;

    public Defence(DefenceData data) {
        this.data = data;
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

    public abstract String getType();

    public abstract int getRadius();

    public abstract int getDamage();

    public abstract int getMaxSimEntities();

    public abstract int getRate();

    public abstract int getLevel();

    public abstract Collection<Entity> getNearbyEntities(World defenceWorld);

    public abstract void enable();

    public abstract void disable();

    public boolean isEnabled() {
        return this.task != null;
    }

    private static void createHologram(Location location, DefenceStruct defence, String playerName) {
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