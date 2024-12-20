package net.skullian.skyfactions.common.module.abstraction;

import net.skullian.skyfactions.common.module.SkyModuleManager;
import net.skullian.skyfactions.common.user.SkyUser;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class DiscordModule {

    public abstract CompletableFuture<String> createLinkCode(SkyUser user);

    public abstract String getUsernameByID(String id);

    public abstract UUID getUUIDFromCode(String code);

    public abstract void onSuccessfulLink(String code);

    public static DiscordModule getInstance() {
        return (DiscordModule) SkyModuleManager.getEnabledModules().get("net.skullian.skyfactions.module.impl.discord.DiscordModule");
    }
}
