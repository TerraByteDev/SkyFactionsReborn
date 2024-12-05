package net.skullian.skyfactions.module;

import lombok.Getter;

public enum SkyModules {

    DISCORD("net.skullian.skyfactions.module.impl.discord.DiscordModule");

    @Getter
    private final String modulePath;

    SkyModules(String path) {
        this.modulePath = path;
    }

    public SkyModule getModule() {
        return SkyModuleManager.getEnabledModules().get(this.modulePath);
    }
}
