package net.skullian.skyfactions.common.api;

import lombok.Getter;
import net.skullian.skyfactions.common.config.ConfigFileHandler;
import net.skullian.skyfactions.common.database.DatabaseManager;
import net.skullian.skyfactions.common.database.cache.CacheService;
import net.skullian.skyfactions.common.util.worldborder.BorderAPI;
import org.jetbrains.annotations.NotNull;

public abstract class SkyApi {

    @Getter private static SkyApi instance = null;

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
     * Set the API Instance. Can depend on the platform (obviously a WIP)
     *
     * @param newInstance The new class that extends the SkyAPI class.
     */
    public static void setInstance(SkyApi newInstance) {
        if (instance != null) throw new IllegalStateException("SkyAPI Instance has already been set!");
        instance = newInstance;
    }

}
