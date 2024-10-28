package net.skullian.skyfactions.npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface SkyNPC {

    public String getId();

    public String getDisplayName();

    public Location getLocation();

    public boolean isPresent();

    public Entity getEntity();

    void remove();
    
}