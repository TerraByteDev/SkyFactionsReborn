package net.skullian.skyfactions.common.module;

public interface SkyModule {

    void onEnable() throws Exception;

    void onReload();

    void onDisable();

    boolean shouldEnable();

}