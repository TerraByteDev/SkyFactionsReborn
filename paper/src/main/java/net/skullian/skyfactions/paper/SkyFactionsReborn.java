package net.skullian.skyfactions.paper;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.paper.api.SpigotSkyAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyFactionsReborn extends JavaPlugin {

    @Override
    public void onEnable() {
        SkyApi.setInstance(new SpigotSkyAPI());
        SkyApi.getInstance().onEnable();
    }

    @Override
    public void onDisable() {
        SkyApi.getInstance().onDisable();
    }

    public void disable() {
        getServer().getPluginManager().disablePlugin(this);
    }

    public static SkyFactionsReborn getInstance() {
        return getPlugin(SkyFactionsReborn.class);
    }
}
