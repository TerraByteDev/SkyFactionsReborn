package net.skullian.skyfactions.core.user;

import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.user.UserManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotUserManager extends UserManager {

    private final Map<UUID, SkyUser> skyUsers = new ConcurrentHashMap<>();

    @Override
    public SkyUser getUser(UUID uuid) {
        if (skyUsers.containsKey(uuid)) return skyUsers.get(uuid);

        SkyUser skyUser = new SpigotSkyUser(uuid);
        skyUsers.put(uuid, skyUser);
        return skyUser;
    }

    @Override
    public boolean isCached(UUID uuid) {
        return skyUsers.containsKey(uuid);
    }


}
