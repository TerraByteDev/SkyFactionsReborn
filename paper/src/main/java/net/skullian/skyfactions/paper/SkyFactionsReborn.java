package net.skullian.skyfactions.paper;

import lombok.Getter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.paper.api.SpigotSkyAPI;
import net.skullian.skyfactions.paper.defence.block.BrokenBlockService;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyFactionsReborn extends JavaPlugin {

    @Getter private static final BrokenBlockService blockService = new BrokenBlockService();

    @Override
    public void onEnable() {


        SkyApi.setInstance(new SpigotSkyAPI());
        SkyApi.setPluginInstance(this);
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
