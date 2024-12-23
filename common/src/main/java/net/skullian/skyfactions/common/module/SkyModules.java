package net.skullian.skyfactions.common.module;

import lombok.Getter;

@Getter
public enum SkyModules {

    DISCORD("net.skullian.skyfactions.module.impl.discord.DiscordModule");

    private final String modulePath;

    SkyModules(String path) {
        this.modulePath = path;
    }

    public SkyModule getModule() {
        return SkyModuleManager.getEnabledModules().get(this.modulePath);
    }
}
