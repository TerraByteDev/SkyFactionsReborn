package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.user.SkyUser;

import java.util.UUID;

public abstract class UserAPI {

    public abstract SkyUser getUser(UUID uuid);

}
