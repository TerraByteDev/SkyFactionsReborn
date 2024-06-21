package net.skullian.torrent.skyfactions;

import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.command.discord.LinkCommand;
import net.skullian.torrent.skyfactions.command.discord.UnlinkCommand;
import net.skullian.torrent.skyfactions.command.gems.GemsCommandHandler;
import net.skullian.torrent.skyfactions.command.gems.GemsCommandTabCompletion;
import net.skullian.torrent.skyfactions.command.island.IslandCommandHandler;
import net.skullian.torrent.skyfactions.command.island.IslandCommandTabCompletion;
import net.skullian.torrent.skyfactions.command.raid.RaidCommandHandler;
import net.skullian.torrent.skyfactions.command.raid.RaidCommandTabCompletion;
import net.skullian.torrent.skyfactions.config.ConfigFileHandler;
import net.skullian.torrent.skyfactions.db.HikariHandler;
import net.skullian.torrent.skyfactions.discord.DiscordHandler;
import net.skullian.torrent.skyfactions.event.PlayerHandler;
import net.skullian.torrent.skyfactions.papi.PlaceholderManager;
import net.skullian.torrent.skyfactions.util.DependencyHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

@Log4j2(topic = "SkyFactionsReborn")
public final class SkyFactionsReborn extends JavaPlugin {

    public static ConfigFileHandler configHandler;
    public static HikariHandler db;
    public static DiscordHandler dc;

    String text = """
            
            ____ _  _ _   _ ____ ____ ____ ___ _ ____ _  _ ____\s
            [__  |_/   \\_/  |___ |__| |     |  | |  | |\\ | [__ \s
            ___] | \\_   |   |    |  | |___  |  | |__| | \\| ___]
            """;

    @Override
    public void onEnable() {
        LOGGER.info(text);

        LOGGER.info("Initialising Configs.");
        configHandler = new ConfigFileHandler();
        configHandler.loadFiles(this);

        LOGGER.info("Registering Commands.");
        getCommand("island").setExecutor(new IslandCommandHandler());
        getCommand("island").setTabCompleter(new IslandCommandTabCompletion());

        getCommand("link").setExecutor(new LinkCommand());
        getCommand("unlink").setExecutor(new UnlinkCommand());

        getCommand("raid").setExecutor(new RaidCommandHandler());
        getCommand("raid").setTabCompleter(new RaidCommandTabCompletion());

        getCommand("gems").setExecutor(new GemsCommandHandler());
        getCommand("gems").setTabCompleter(new GemsCommandTabCompletion());

        LOGGER.info("Registering Events.");
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);

        LOGGER.info("Initialising JDA / Discord.");
        dc = new DiscordHandler();
        dc.initialiseBot();

        LOGGER.info("Handling optional dependencies.");
        DependencyHandler.init();

        initialiseDatabaseConnection();
    }

    @Override
    public void onDisable() {
        closeDatabase();
        dc.disconnect();
        LOGGER.info(text);
        LOGGER.info("SkyFactions has been disabled.");
    }

    private void initialiseDatabaseConnection() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            new File(getDataFolder(), "/data").mkdir();

            // Initialise the database.
            db = new HikariHandler();
            db.initialise(configHandler.SETTINGS_CONFIG.getString("Database.TYPE"));

            // Cache the most recent ID.
            db.setCachedNextID();
        } catch (SQLException error) {
            LOGGER.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            LOGGER.fatal("There was an error initialising the database.");
            LOGGER.fatal("Please check the database for any configuration mistakes.");
            LOGGER.fatal("Plugin will now disable.");
            LOGGER.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            error.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void closeDatabase() {
        try {
            db.closeConnection();
        } catch (SQLException error) {
            LOGGER.error("----------------------- DATABASE EXCEPTION -----------------------");
            LOGGER.error("There was an error initialising the database.");
            LOGGER.error("Please check the database for any configuration mistakes.");
            LOGGER.error("----------------------- DATABASE EXCEPTION -----------------------");
            error.printStackTrace();
        }
    }

    public static SkyFactionsReborn getInstance() { return getPlugin(SkyFactionsReborn.class); }
}
