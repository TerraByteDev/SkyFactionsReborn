package net.skullian.skyfactions.paper.user;

import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.user.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotUserManager extends UserManager {

    private final Map<UUID, SkyUser> skyUsers = new ConcurrentHashMap<>();

    @Override
    public SkyUser getUser(UUID uuid) {
        if (skyUsers.containsKey(uuid)) return skyUsers.get(uuid);

        SkyUser skyUser = new SpigotSkyUser(uuid, false, null);
        skyUsers.put(uuid, skyUser);
        return skyUser;
    }

    @Override
    public boolean isCached(UUID uuid) {
        return skyUsers.containsKey(uuid);
    }

    @Override
    public SkyUser cloudFetch(UUID uuid, Object commandSender, boolean console) {
        if (skyUsers.containsKey(uuid)) return skyUsers.get(uuid).setCommandSender(commandSender);

        SkyUser skyUser = new SpigotSkyUser(uuid, console, commandSender);
        if (console) skyUsers.put(uuid, skyUser);
        return skyUser;
    }


}
