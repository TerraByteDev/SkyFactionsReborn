package net.skullian.skyfactions.module;

public interface SkyModule {

    void onEnable() throws Exception;

    void onReload();

    void onDisable();

    boolean shouldEnable();

}