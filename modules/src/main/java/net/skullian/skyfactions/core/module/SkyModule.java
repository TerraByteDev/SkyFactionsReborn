package net.skullian.skyfactions.core.module;

public interface SkyModule {

    void onEnable() throws Exception;

    void onReload();

    void onDisable();

    boolean shouldEnable();

}