package net.skullian.skyfactions.common.npc;

import net.skullian.skyfactions.common.util.SkyLocation;

public interface SkyNPC {

    public String getId();

    public String getDisplayName();

    public SkyLocation getLocation();

    public boolean isPresent();
    
    public Object getEntity();

    public String getEntityType();

    public void updateDisplayName(String name);

    public void updateEntityType(String type);

    public void updateSkin(String skin);

    void remove();
    
}