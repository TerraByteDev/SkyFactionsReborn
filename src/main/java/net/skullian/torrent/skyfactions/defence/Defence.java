package net.skullian.torrent.skyfactions.defence;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;

public abstract class Defence {

    public abstract String getType();

    public abstract int getRadius();

    public abstract int getDamage();

    public abstract int getMaxSimEntities();

    public abstract int getRate();

    public abstract int getLevel();

    public abstract Collection<Entity> getNearbyEntities(World defenceWorld);

    public abstract void enable();

    public abstract void disable();
}