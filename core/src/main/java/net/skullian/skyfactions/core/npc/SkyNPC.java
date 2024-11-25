package net.skullian.skyfactions.core.npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface SkyNPC {

    public String getId();

    public String getDisplayName();

    public Location getLocation();

    public boolean isPresent();
    
    public Entity getEntity();

    public EntityType getEntityType();

    public void updateDisplayName(String name);

    public void updateEntityType(EntityType type);

    public void updateSkin(String skin);

    void remove();
    
}