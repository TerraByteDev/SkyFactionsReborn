package net.skullian.skyfactions.common.api;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.skullian.skyfactions.common.config.ConfigFileHandler;
import net.skullian.skyfactions.common.database.DatabaseManager;
import net.skullian.skyfactions.common.database.cache.CacheService;
import net.skullian.skyfactions.common.defence.DefenceFactory;
import net.skullian.skyfactions.common.gui.UIShower;
import net.skullian.skyfactions.common.npc.NPCManager;
import net.skullian.skyfactions.common.user.UserManager;
import net.skullian.skyfactions.common.util.worldborder.BorderAPI;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public abstract class SkyApi {

    @Getter
    private static SkyApi instance = null;
    private static Object pluginInstance = null;

    /**
     * Get the configuration handler. Not much need for this usually.
     *
     * @return {@link ConfigFileHandler} the initialised config handler.
     */
    @NotNull public abstract ConfigFileHandler getConfigHandler();

    /**
     * Get the database manager. This is used for all database operations.
     *
     * @return {@link DatabaseManager} the initialised database manager.
     */
    @NotNull public abstract DatabaseManager getDatabaseManager();

    /**
     * Get the WorldBorderAPI implementation. This is, obviously, for controlling the per-player world borders.
     *
     * @return {@link BorderAPI} - The initialised border API.
     */
    @NotNull public abstract BorderAPI getWorldBorderAPI();

    /**
     * Get the cache service. Used for caching data and periodically
     * saving it to the database.
     * This avoids repetitive database calls, which isn't ideal.
     *
     * @return {@link CacheService}
     */
    @NotNull public abstract CacheService getCacheService();

    /**
     * Get the DefenceAPI implementation. This is used for all defence-related operations. (Most of them anyway).
     *
     * @return {@link DefenceAPI}
     */
    @NotNull public abstract DefenceAPI getDefenceAPI();

    /**
     * Get the RaidAPI implementation. This is used for all raid-related operations.
     *
     * @return {@link RaidAPI}
     */
    @NotNull public abstract RaidAPI getRaidAPI();

    /**
     * Get the RegionAPI implementation. This involves region-related operations such as removing / adding regions, teleporting players and pasting schematics.
     *
     * @return {@link RegionAPI}
     */
    @NotNull public abstract RegionAPI getRegionAPI();

    /**
     * Get the RunesAPI. This allows you to control the runes system for players.
     *
     * @return {@link RunesAPI}
     */
   @NotNull public abstract RunesAPI getRunesAPI();

    /**
     * Get the PlayerAPI implementation. This allows you to fetch / store data concerning players such as discord IDs, locales, etc.
     *
     * @return {@link PlayerAPI}
     */
   @NotNull public abstract PlayerAPI getPlayerAPI();

    /**
     * Get the Notification API. This allows you to send notifications to players, which persist on relogs.
     *
     * @return {@link NotificationAPI}
     */
   @NotNull public abstract NotificationAPI getNotificationAPI();

    /**
     * Get the User Manager. This allows you to fetch / store data concerning users such as their balance, island, etc.
     *
     * @return {@link UserManager}
     */
   @NotNull public abstract UserManager getUserManager();

    /**
     * Get the Gems API. This allows you to control the gems system for players..
     * Kind of self-explanatory.
     *
     * @return {@link GemsAPI}
     */
   @NotNull public abstract GemsAPI getGemsAPI();

    /**
     * Get the Island API. This is typically used for only player-island related operations.
     * Can vary ^^
     *
     * @return {@link IslandAPI}
     */
   @NotNull public abstract IslandAPI getIslandAPI();

    /**
     * Get the Faction API. This is typically used for only faction-related operations.
     *
     * @return {@link FactionAPI}
     */
   @NotNull public abstract FactionAPI getFactionAPI();

    /**
     * Get the Sound API. This is for playing sounds to players.
     *
     * @return {@link SoundAPI}
     */
   @NotNull public abstract SoundAPI getSoundAPI();

    /**
     * Get the File API. This is for file-related operations such as config paths, etc.
     * @return {@link FileAPI}
     */
   @NotNull public abstract FileAPI getFileAPI();

    /**
     * Get the NPC Manager.
     * Self explanatory.
     *
     * @return {@link NPCManager}
     */
   @NotNull public abstract NPCManager getNPCManager();

    /**
     * API For the Obelisk. Used for spawning obelisk blocks on island creation.
     *
     * @return {@link ObeliskAPI}
     */
   @NotNull public abstract ObeliskAPI getObeliskAPI();

    /**
     * Get the UIShower. This is used for showing GUIs to players for platforms.
     *
     * @return {@link UIShower}
     */
   @NotNull public abstract UIShower getUIShower();

    /**
     * Get the Defence Factory. This is used for creating defences.
     *
     * @return {@link DefenceFactory}
     */
   @NotNull public abstract DefenceFactory getDefenceFactory();

    /**
     * Get the Audience. This is used for sending messages to console.
     *
     * @return {@link Audience}
     */
   @NotNull public abstract Audience getConsoleAudience();

    /**
     * Set the API Instance. Can depend on the platform (obviously a WIP)
     *
     * @param newInstance The new class that extends the SkyAPI class.
     */
    public static void setInstance(SkyApi newInstance) {
        if (instance != null) throw new IllegalStateException("SkyAPI Instance has already been set!");
        instance = newInstance;
    }

    public static void setPluginInstance(Object newInstance) {
        if (pluginInstance != null) throw new IllegalStateException("Plugin instance has already been set!");
        pluginInstance = newInstance;
    }

    public static void disablePlugin() {
        try {
            if (pluginInstance == null) return;
            Class<?> actualClass = pluginInstance.getClass();

            Method method;
            try {
                method = actualClass.getMethod("disable");
            } catch (NoSuchMethodError exception) {
                method = actualClass.getDeclaredMethod("disable");
            }

            method.setAccessible(true);
            method.invoke(instance);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

}
