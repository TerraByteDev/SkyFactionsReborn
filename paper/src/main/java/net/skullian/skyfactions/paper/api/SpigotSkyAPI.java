package net.skullian.skyfactions.paper.api;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.skullian.skyfactions.common.api.*;
import net.skullian.skyfactions.common.config.ConfigFileHandler;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.DatabaseManager;
import net.skullian.skyfactions.common.database.cache.CacheService;
import net.skullian.skyfactions.common.defence.DefenceFactory;
import net.skullian.skyfactions.common.gui.UIShower;
import net.skullian.skyfactions.common.npc.NPCManager;
import net.skullian.skyfactions.common.user.UserManager;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.util.worldborder.BorderAPI;
import net.skullian.skyfactions.common.util.worldborder.persistence.Border;
import net.skullian.skyfactions.common.util.worldborder.persistence.BorderPersistence;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.defence.SpigotDefencesFactory;
import net.skullian.skyfactions.paper.gui.SpigotUIShower;
import net.skullian.skyfactions.paper.npc.SpigotNPCManager;
import net.skullian.skyfactions.paper.user.SpigotUserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.SQLException;

public final class SpigotSkyAPI extends SkyApi {

    private ConfigFileHandler configHandler;
    private DatabaseManager databaseManager;
    private BorderAPI worldBorderAPI;
    private CacheService cacheService;
    private DefenceAPI defenceAPI;
    private RaidAPI raidAPI;
    private RegionAPI regionAPI;
    private RunesAPI runesAPI;
    private PlayerAPI playerAPI;
    private NotificationAPI notificationAPI;
    private UserManager userManager;
    private GemsAPI gemsAPI;
    private IslandAPI islandAPI;
    private FactionAPI factionAPI;
    private SoundAPI soundAPI;
    private FileAPI fileAPI;
    private NPCManager npcManager;
    private ObeliskAPI obeliskAPI;
    private UIShower uiShower;
    private DefenceFactory defenceFactory;
    private BukkitAudiences audience;

    public SpigotSkyAPI() {
        try {
            // Store an instance of the ConfigHandler class in case it is needed.
            // Primarily used for the discord integration.
            SLogger.info("Initialising Configs.");
            configHandler = new ConfigFileHandler();
            configHandler.loadFiles();


            new File(SkyFactionsReborn.getInstance().getDataFolder(), "/data").mkdir();
            databaseManager = new DatabaseManager();
            databaseManager.initialise(Settings.DATABASE_TYPE.getString());

            SLogger.info("Registering World Border service provider.");
            BorderAPI bApi = new BorderPersistence(new Border(), SkyFactionsReborn.getInstance());
            Bukkit.getServer().getServicesManager().register(BorderAPI.class, bApi, SkyFactionsReborn.getInstance(), ServicePriority.High);
            worldBorderAPI = bApi;

            SLogger.info("Initialising Cache Service.");
            cacheService = new CacheService();
            cacheService.enable();

            SLogger.info("Initialising APIs.");
            defenceAPI = new SpigotDefenceAPI();
            raidAPI = new SpigotRaidAPI();
            regionAPI = new SpigotRegionAPI();
            runesAPI = new SpigotRunesAPI();
            playerAPI = new SpigotPlayerAPI();
            notificationAPI = new SpigotNotificationAPI();
            userManager = new SpigotUserManager();
            gemsAPI = new SpigotGemsAPI();
            islandAPI = new SpigotIslandAPI();
            factionAPI = new SpigotFactionAPI();
            soundAPI = new SpigotSoundAPI();
            fileAPI = new SpigotFileAPI();
            npcManager = new SpigotNPCManager();
            obeliskAPI = new SpigotObeliskAPI();
            uiShower = new SpigotUIShower();
            defenceFactory = new SpigotDefencesFactory();
            audience = BukkitAudiences.create(SkyFactionsReborn.getInstance());

        } catch (SQLException error) {
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            SLogger.fatal("There was an error initialising the database.");
            SLogger.fatal("Please check the database for any configuration mistakes.");
            SLogger.fatal("Plugin will now disable.");
            SLogger.fatal("----------------------- DATABASE EXCEPTION -----------------------");
            error.printStackTrace();
            SkyFactionsReborn.getInstance().disable();
        }
    }

    @Override
    public @NotNull ConfigFileHandler getConfigHandler() {
        return this.configHandler;
    }

    @NotNull
    @Override
    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    @NotNull
    @Override
    public BorderAPI getWorldBorderAPI() {
        return this.worldBorderAPI;
    }

    @NotNull
    @Override
    public CacheService getCacheService() {
        return this.cacheService;
    }

    @NotNull
    @Override
    public DefenceAPI getDefenceAPI() {
        return this.defenceAPI;
    }

    @NotNull
    @Override
    public RaidAPI getRaidAPI() {
        return this.raidAPI;
    }

    @NotNull
    @Override
    public RegionAPI getRegionAPI() {
        return this.regionAPI;
    }

    @NotNull
    @Override
    public RunesAPI getRunesAPI() {
        return this.runesAPI;
    }

    @NotNull
    @Override
    public PlayerAPI getPlayerAPI() {
        return playerAPI;
    }

    @NotNull
    @Override
    public NotificationAPI getNotificationAPI() {
        return notificationAPI;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @NotNull
    @Override
    public GemsAPI getGemsAPI() {
        return gemsAPI;
    }

    @NotNull
    @Override
    public IslandAPI getIslandAPI() {
        return islandAPI;
    }

    @NotNull
    @Override
    public FactionAPI getFactionAPI() {
        return factionAPI;
    }

    @NotNull
    @Override
    public SoundAPI getSoundAPI() {
        return soundAPI;
    }

    @NotNull
    @Override
    public FileAPI getFileAPI() {
        return fileAPI;
    }

    @NotNull
    @Override
    public NPCManager getNPCManager() {
        return npcManager;
    }

    @NotNull
    @Override
    public ObeliskAPI getObeliskAPI() {
        return obeliskAPI;
    }

    @NotNull
    @Override
    public UIShower getUIShower() {
        return uiShower;
    }

    @NotNull
    @Override
    public DefenceFactory getDefenceFactory() {
        return defenceFactory;
    }

    @NotNull
    @Override
    public Audience getConsoleAudience() {
        return audience.console();
    }
}
