package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.island.SkyIsland;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyLocation;

public abstract class ObeliskAPI {

    public abstract void spawnPlayerObelisk(SkyUser player, SkyIsland island);

    public abstract void spawnFactionObelisk(String faction, SkyIsland island);

    public abstract void applyPersistentData(String ownerIdentifier, SkyLocation location, String type);

    public abstract void onIslandDelete(SkyIsland island, String type);

    public abstract void preLoad();
}
