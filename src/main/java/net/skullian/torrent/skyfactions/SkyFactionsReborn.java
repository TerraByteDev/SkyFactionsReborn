package net.skullian.torrent.skyfactions;

import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.jeff_media.customblockdata.CustomBlockData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.skullian.torrent.skyfactions.command.discord.LinkCommand;
import net.skullian.torrent.skyfactions.command.discord.UnlinkCommand;
import net.skullian.torrent.skyfactions.command.faction.FactionCommandHandler;
import net.skullian.torrent.skyfactions.command.faction.FactionCommandTabCompletion;
import net.skullian.torrent.skyfactions.command.gems.GemsCommandHandler;
import net.skullian.torrent.skyfactions.command.gems.GemsCommandTabCompletion;
import net.skullian.torrent.skyfactions.command.island.IslandCommandHandler;
import net.skullian.torrent.skyfactions.command.island.IslandCommandTabCompletion;
import net.skullian.torrent.skyfactions.command.raid.RaidCommandHandler;
import net.skullian.torrent.skyfactions.command.raid.RaidCommandTabCompletion;
import net.skullian.torrent.skyfactions.command.runes.RunesCommandHandler;
import net.skullian.torrent.skyfactions.command.runes.RunesCommandTabCompletion;
import net.skullian.torrent.skyfactions.command.sf.SFCommandHandler;
import net.skullian.torrent.skyfactions.command.sf.SFCommandTabCompletion;
import net.skullian.torrent.skyfactions.config.ConfigFileHandler;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.db.HikariHandler;
import net.skullian.torrent.skyfactions.discord.DiscordHandler;
import net.skullian.torrent.skyfactions.event.DefenceHandler;
import net.skullian.torrent.skyfactions.event.ObeliskInteractionListener;
import net.skullian.torrent.skyfactions.event.PlayerHandler;
import net.skullian.torrent.skyfactions.util.DependencyHandler;
import net.skullian.torrent.skyfactions.util.EconomyHandler;
import net.skullian.torrent.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;


public final class SkyFactionsReborn extends JavaPlugin {

    public static ConfigFileHandler configHandler;
    public static HikariHandler db;
    public static DiscordHandler dc;
    public static EconomyHandler ec;
    public static WorldBorderApi worldBorderApi;

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
    public void onEnable() {

        print();
        // Store an instance of the ConfigHandler class in case it is needed.
        // Primarily used for the discord integration.
        SLogger.info("Initialising Configs.");
        configHandler = new ConfigFileHandler();
        configHandler.loadFiles(this); // Load all files (and create them if they don't exist already).

        SLogger.info("Initialising Economy.");
        ec = new EconomyHandler();
        ec.init(this);

        SLogger.info("Registering Commands.");
        getCommand("island").setExecutor(new IslandCommandHandler());
        getCommand("island").setTabCompleter(new IslandCommandTabCompletion());

        // There is the option to disable the discord integration if you don't want it.
        // To avoid later confusion, we only register the discord related commands if it is enabled.
        boolean discordEnabled = SkyFactionsReborn.configHandler.DISCORD_CONFIG.getBoolean("Discord.ENABLED");
        if (discordEnabled) {
            getCommand("link").setExecutor(new LinkCommand());
            getCommand("unlink").setExecutor(new UnlinkCommand());
        }

        getCommand("raid").setExecutor(new RaidCommandHandler());
        getCommand("raid").setTabCompleter(new RaidCommandTabCompletion());

        getCommand("gems").setExecutor(new GemsCommandHandler());
        getCommand("gems").setTabCompleter(new GemsCommandTabCompletion());

        getCommand("sf").setExecutor(new SFCommandHandler());
        getCommand("sf").setTabCompleter(new SFCommandTabCompletion());

        getCommand("faction").setExecutor(new FactionCommandHandler());
        getCommand("faction").setTabCompleter(new FactionCommandTabCompletion());

        getCommand("runes").setExecutor(new RunesCommandHandler());
        getCommand("runes").setTabCompleter(new RunesCommandTabCompletion());

        SLogger.info("Registering Events.");
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
        getServer().getPluginManager().registerEvents(new ObeliskInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new DefenceHandler(), this);

        SLogger.info("Registering PacketEvents Listener.");

        // We store an instance of the DiscordHandler class as that is how other internals
        // access methods related to Discord (e.g. raid notifications).
        SLogger.info("Initialising JDA / Discord.");
        dc = new DiscordHandler();
        dc.initialiseBot();

        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        if (worldBorderApiRegisteredServiceProvider == null) {
            getLogger().info("API not found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();

        // This is kind of pointless.
        // Just a class for handling dependencies and optional dependencies.
        // Majorly incomplete.
        SLogger.info("Handling optional dependencies.");
        DependencyHandler.init();

        // Player Data Container (CustomBlockData API) listener.
        SLogger.info("Handling PDC Listener.");
        CustomBlockData.registerListener(this);

        // Initialise the database last.
        initialiseDatabaseConnection();
    }

    @Override
    public void onDisable() {
        SLogger.info("Closing Database connection.");
        closeDatabase();
        dc.disconnect();

        print();
        SLogger.info("SkyFactions has been disabled.");
    }

    private void initialiseDatabaseConnection() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            new File(getDataFolder(), "/data").mkdir();

            // Initialise the database.
            db = new HikariHandler();
            db.initialise(Settings.DATABASE_TYPE.getString());

            // Cache the most recent ID.
            db.setIslandCachedNextID();
            db.setFactionCachedNextID();
        } catch (SQLException error) {
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            SLogger.fatal("There was an error initialising the database.");
            SLogger.fatal("Please check the database for any configuration mistakes.");
            SLogger.fatal("Plugin will now disable.");
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            error.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void closeDatabase() {
        try {
            if (db != null) {
                db.closeConnection();
            }
        } catch (SQLException error) {
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            SLogger.fatal("There was an error initialising the database.");
            SLogger.fatal("Please check the database for any configuration mistakes.");
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            error.printStackTrace();
        }
    }

    public static SkyFactionsReborn getInstance() {
        return getPlugin(SkyFactionsReborn.class);
    }
}
