package net.skullian.skyfactions.common.npc.factory;

import net.skullian.skyfactions.common.npc.SkyNPC;
import net.skullian.skyfactions.common.util.SkyLocation;

public interface SkyNPCFactory {

    boolean isNPC(Object entity);

    SkyNPC create(String id, String name, SkyLocation location, String skin, Object entityType, boolean isFaction);

    SkyNPC getNPC(String id, boolean faction);
    
}