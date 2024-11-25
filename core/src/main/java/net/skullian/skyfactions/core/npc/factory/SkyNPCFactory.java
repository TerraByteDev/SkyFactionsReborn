package net.skullian.skyfactions.core.npc.factory;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import net.skullian.skyfactions.core.npc.SkyNPC;

public interface SkyNPCFactory {

    boolean isNPC(Entity entity);

    SkyNPC create(String id, String name, Location location, String skin, EntityType entityType, boolean isFaction);

    SkyNPC getNPC(String id, boolean faction);
    
}