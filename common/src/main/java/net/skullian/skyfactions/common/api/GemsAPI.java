package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.UUID;

public abstract class GemsAPI {

    public abstract void addGems(UUID playerUUID, int addition);

    public abstract void subtractGems(UUID playerUUID, int subtraction);

    public abstract SkyItemStack createGemsStack(SkyUser player);

    public abstract boolean isGemsStack(SkyItemStack stack);

}
