package net.skullian.skyfactions.common.user;

import java.util.UUID;

public abstract class UserManager {

    public abstract SkyUser getUser(UUID uuid);

    public abstract SkyUser getUser(String name);

    public abstract boolean isCached(UUID uuid);

    public abstract SkyUser cloudFetch(UUID uuid, Object commandSender, boolean console);

}
