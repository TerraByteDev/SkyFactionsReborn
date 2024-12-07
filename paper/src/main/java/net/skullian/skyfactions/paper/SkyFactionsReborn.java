package net.skullian.skyfactions.paper;

import com.jeff_media.customblockdata.CustomBlockData;
import lombok.Getter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.module.SkyModuleManager;
import net.skullian.skyfactions.module.impl.discord.DiscordModule;
import net.skullian.skyfactions.paper.api.SpigotSkyAPI;
import net.skullian.skyfactions.paper.defence.block.BrokenBlockService;
import net.skullian.skyfactions.paper.event.PlayerListener;
import net.skullian.skyfactions.paper.event.armor.ArmorListener;
import net.skullian.skyfactions.paper.event.defence.DefenceDamageHandler;
import net.skullian.skyfactions.paper.event.defence.DefenceInteractionHandler;
import net.skullian.skyfactions.paper.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.paper.event.ObeliskInteractionListener;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

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
