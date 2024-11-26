package net.skullian.skyfactions.core;

import com.github.retrooper.packetevents.PacketEvents;
import com.jeff_media.customblockdata.CustomBlockData;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.core.api.SpigotSkyAPI;
import net.skullian.skyfactions.core.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.core.command.gems.GemsCommandHandler;
import net.skullian.skyfactions.core.command.island.IslandCommandHandler;
import net.skullian.skyfactions.core.command.raid.RaidCommandHandler;
import net.skullian.skyfactions.core.command.runes.RunesCommandHandler;
import net.skullian.skyfactions.core.command.sf.SFCommandHandler;
import net.skullian.skyfactions.core.module.SkyModuleManager;
import net.skullian.skyfactions.common.database.DatabaseManager;
import net.skullian.skyfactions.common.database.cache.CacheService;
import net.skullian.skyfactions.core.defence.block.BrokenBlockService;
import net.skullian.skyfactions.core.event.PlayerListener;
import net.skullian.skyfactions.core.event.armor.ArmorListener;
import net.skullian.skyfactions.core.event.defence.DefenceDamageHandler;
import net.skullian.skyfactions.core.event.defence.DefenceInteractionHandler;
import net.skullian.skyfactions.core.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.core.event.ObeliskInteractionListener;
import net.skullian.skyfactions.core.npc.NPCManager;
import net.skullian.skyfactions.common.util.DependencyHandler;
import net.skullian.skyfactions.common.util.nms.NMSProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SkyFactionsReborn extends JavaPlugin {

    @Getter private static final BrokenBlockService blockService = new BrokenBlockService();
    @Getter private static NPCManager npcManager;
    @Getter private static SkyModuleManager moduleManager;

    private void print() {
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
    public void onLoad() {
        SLogger.info("Initialising PacketEvents.");

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {

        print();

        SLogger.info("Initialising NMS Provider.");
        NMSProvider.init();

        SLogger.info("Registering SkyAPI.");
        SkyApi.setInstance(new SpigotSkyAPI());

        SLogger.info("Initialising NPC Manager.");
        npcManager = new NPCManager();

        SLogger.info("Initialising Module Manager.");
        moduleManager = new SkyModuleManager();
        moduleManager.onEnable();

        SLogger.info("Loading InvLib Instance.");
        InvUI.getInstance().setPlugin(this);

        new GemsCommandHandler();
        new FactionCommandHandler();
        new IslandCommandHandler();
        new RaidCommandHandler();
        new RunesCommandHandler();
        new SFCommandHandler();

        SLogger.info("Registering Events.");
        getServer().getPluginManager().registerEvents(new ArmorListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ObeliskInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new DefenceDamageHandler(), this);
        getServer().getPluginManager().registerEvents(new DefencePlacementHandler(), this);
        getServer().getPluginManager().registerEvents(new DefenceInteractionHandler(), this);

        SLogger.info("Creating PacketEvents Instance.");
        PacketEvents.getAPI().init();

        SLogger.info("Initialising EntityLib.");
        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
                .usePlatformLogger();

        EntityLib.init(platform, settings);

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
        try {
            if (SkyApi.getInstance().getCacheService() != null) {
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.scheduleAtFixedRate(() -> SLogger.info("Waiting for periodic cache service to disable..."), 0, 5, TimeUnit.SECONDS);
                SkyApi.getInstance().getCacheService().disable().get();

                scheduler.shutdownNow();
            }

            SLogger.info("Disabling Module Manager.");
            moduleManager.onDisable();

            SLogger.info("Closing Database connection.");
            closeDatabase();

            SLogger.info("Terminating PacketEvents.");
            PacketEvents.getAPI().terminate();

            print();
            SLogger.info("SkyFactions has been disabled.");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to disable Cache Service: " + e.getMessage(), e);
        }
    }

    public void disable() {
        getServer().getPluginManager().disablePlugin(this);
    }

    private void closeDatabase() {
        DatabaseManager databaseManager = SkyApi.getInstance().getDatabaseManager();
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
    }

    public static SkyFactionsReborn getInstance() {
        return getPlugin(SkyFactionsReborn.class);
    }
}
