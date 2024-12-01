package net.skullian.skyfactions.paper;

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
import net.skullian.skyfactions.paper.api.SpigotSkyAPI;
import net.skullian.skyfactions.paper.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.paper.command.gems.GemsCommandHandler;
import net.skullian.skyfactions.paper.command.island.IslandCommandHandler;
import net.skullian.skyfactions.paper.command.raid.RaidCommandHandler;
import net.skullian.skyfactions.paper.command.runes.RunesCommandHandler;
import net.skullian.skyfactions.paper.command.sf.SFCommandHandler;
import net.skullian.skyfactions.common.module.SkyModuleManager;
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
import net.skullian.skyfactions.common.util.nms.NMSProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SkyFactionsReborn extends JavaPlugin {

    @Getter private static final BrokenBlockService blockService = new BrokenBlockService();
    @Getter private static NPCManager npcManager;

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

        SLogger.info("Registering SkyAPI & Plugin instance.");
        SkyApi.setInstance(new SpigotSkyAPI());
        SkyApi.setPluginInstance(this);

        SLogger.info("Initialising NPC Manager.");
        npcManager = new SpigotNPCManager();

        SLogger.info("Initialising Module Manager.");
        SkyModuleManager.onEnable();

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
            SkyApi.getInstance().getCacheService();
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(() -> SLogger.info("Waiting for periodic cache service to disable..."), 0, 5, TimeUnit.SECONDS);
            SkyApi.getInstance().getCacheService().disable().get();

            scheduler.shutdownNow();

            SLogger.info("Disabling Module Manager.");
            SkyModuleManager.onDisable();

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
        SkyApi.getInstance().getDatabaseManager().closeConnection();
    }

    public static SkyFactionsReborn getInstance() {
        return getPlugin(SkyFactionsReborn.class);
    }
}
