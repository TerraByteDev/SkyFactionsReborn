package net.skullian.torrent.skyfactions.defence;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;

public class ArrowDefence extends Defence {

    private String name;
    private int radius;
    private int damage;
    private int maxSimEntities;
    private int rate;
    private int level;
    private Location location;

    public ArrowDefence(String name, int radius, int damage, int maxSimEntities, int rate, int level, Location loc) {
        this.name = name;
        this.radius = radius;
        this.damage = damage;
        this.maxSimEntities = maxSimEntities;
        this.rate = rate;
        this.level = level;
        this.location = loc;
    }

    @Override
    public String getType() {
        return name;
    }

    @Override
    public int getRadius() {
        return 0;
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public int getMaxSimEntities() {
        return 0;
    }

    @Override
    public int getRate() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public Collection<Entity> getNearbyEntities(World defenceWorld) {
        return defenceWorld.getNearbyEntities(location, radius, radius, radius);
    }

    @Override
    public void enable() {

    }
}
