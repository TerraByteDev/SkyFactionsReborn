package net.skullian.skyfactions.common.user;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public abstract class UserManager {

    public abstract SkyUser getUser(UUID uuid);

    public abstract boolean isCached(UUID uuid);

    public abstract SkyUser fromConsole();

}
