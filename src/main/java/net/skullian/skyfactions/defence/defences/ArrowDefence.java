package net.skullian.skyfactions.defence.defences;

import net.skullian.skyfactions.defence.Defence;
import net.skullian.skyfactions.defence.struct.DefenceData;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;

public class ArrowDefence extends Defence {
    public ArrowDefence(DefenceData data) {
        super(data);
    }

    @Override
    public String getType() {
        
        return "";
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
        return List.of();
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}
