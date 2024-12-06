package net.skullian.skyfactions.paper;

import com.jeff_media.customblockdata.CustomBlockData;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.module.SkyModuleManager;
import net.skullian.skyfactions.module.impl.discord.DiscordModule;
import net.skullian.skyfactions.paper.api.SpigotSkyAPI;
import net.skullian.skyfactions.paper.npc.SpigotNPCManager;
import net.skullian.skyfactions.paper.defence.block.BrokenBlockService;
import net.skullian.skyfactions.paper.event.PlayerListener;
import net.skullian.skyfactions.paper.event.armor.ArmorListener;
import net.skullian.skyfactions.paper.event.defence.DefenceDamageHandler;
import net.skullian.skyfactions.paper.event.defence.DefenceInteractionHandler;
import net.skullian.skyfactions.paper.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.paper.event.ObeliskInteractionListener;
import net.skullian.skyfactions.common.npc.NPCManager;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SkyFactionsReborn extends JavaPlugin {

    @Getter private static final BrokenBlockService blockService = new BrokenBlockService();
    @Getter private static NPCManager npcManager;

    public void print() {
        ComponentLogger LOGGER = ComponentLogger.logger("SkyFactionsReborn");
        Style style = Style.style(TextColor.color(25, 100, 230), TextDecoration.BOLD);
        LOGGER.info(Component.text("╭────────────────────────────────────────────────────────────╮").style(style));
        LOGGER.info(Component.text("│                                                            │").style(style));
        LOGGER.info(Component.text("│    ____ _  _ _   _ ____ ____ ____ ___ _ ____ _  _ ____     │").style(style));
        LOGGER.info(Component.text("│    [__  |_/   \\_/  |___ |__| |     |  | |  | |\\ | [__      │").style(style));
        LOGGER.info(Component.text("│    ___] | \\_   |   |    |  | |___  |  | |__| | \\| ___]     │").style(style));
        LOGGER.info(Component.text("│                                                            │").style(style));
        LOGGER.info(Component.text("╰────────────────────────────────────────────────────────────╯").style(style));
    }

    @Override
    public void onEnable() {
        SkyApi.setInstance(new SpigotSkyAPI());
        SkyApi.setPluginInstance(this);
        SkyApi.getInstance().onEnable();
        SLogger.info("Registered SkyAPI & Plugin instance.");

        SkyModuleManager.registerModule(DiscordModule.class);

        print();

        SLogger.info("Initialising NPC Manager.");
        npcManager = new SpigotNPCManager();

        SLogger.info("Initialising Module Manager.");
        SkyModuleManager.onEnable();

        SLogger.info("Loading InvLib Instance.");
        InvUI.getInstance().setPlugin(this);

        SLogger.info("Registering Events.");
        getServer().getPluginManager().registerEvents(new ArmorListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ObeliskInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new DefenceDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new DefencePlacementHandler(), this);
        getServer().getPluginManager().registerEvents(new DefenceInteractionHandler(), this);

        // This is kind of pointless.
        // Just a class for handling dependencies and optional dependencies.
        // Majorly incomplete.
        SLogger.info("Handling optional dependencies.");
        DependencyHandler.init();

        // Player Data Container (CustomBlockData API) listener.
        SLogger.info("Handling PDC Listener.");
        CustomBlockData.registerListener(this);
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
