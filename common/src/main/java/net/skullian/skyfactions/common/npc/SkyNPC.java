package net.skullian.skyfactions.common.npc;

import net.skullian.skyfactions.common.util.SkyLocation;

public interface SkyNPC {

    String getId();

    String getDisplayName();

    SkyLocation getLocation();

    boolean isPresent();
    
    Object getEntity();

    String getEntityType();

    void updateDisplayName(String name);

    void updateEntityType(String type);

    void updateSkin(String skin);

    void remove();
    
}