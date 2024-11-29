package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class PlayerAPI {

    /**
     * Check if a player is registered.
     * Will immediately return a completed future of true if the player is cached.
     * Otherwise, it will query the database and return a future of the result.
     *
     * @param uuid UUID of the player to check.
     *
     * @return {@link CompletableFuture<Boolean>} true if the player is registered.
     */
    public abstract CompletableFuture<Boolean> isPlayerRegistered(UUID uuid);

    /**
     * Cache player data.
     * This method is used to cache all player data on join.
     * This method will not re-fetch any data if the player is already cached.
     *
     * @param uuid UUID of the player to cache.
     */
    public abstract void cacheData(UUID uuid);

    /**
     * Check if a player is cached already locally.
     *
     * @param uuid UUID of the player to check.
     *
     * @return true if the player is already cached.
     */
    public abstract boolean isPlayerCached(UUID uuid);

    /**
     * Get the player data.
     * Will immediately return a completed future if the player is cached.
     * This should always happen unless you are fetching offline playerdata, e.g. for raid related data.
     *
     * @param uuid UUID of the player to fetch,
     *
     * @return {@link CompletableFuture<PlayerData>}
     */
    public abstract CompletableFuture<PlayerData> getPlayerData(UUID uuid);

    /**
     * Get the player data if cached.
     * Will return null if not present, and will not recache.
     *
     * @param uuid UUID of the player to fetch.
     *
     * @return {@link PlayerData} - Cached playerdata object.
     */
    @NotNull public abstract PlayerData getCachedPlayerData(UUID uuid);

    /**
     * Get the locale of a player.
     * This does not query the database because the locales of players would only be fetched while they are online, where their data is cached.
     * If you are trying to fetch a player's locale while they are offline then your implementation is probably not ideal.
     * If you really need to fetch an offline player's locale, just use {@link #getPlayerData(UUID)} and get the locale from the {@link PlayerData} object.
     *
     * @param uuid UUID of the player to get the locale of.
     *
     * @return The locale of the player.
     */
    public abstract String getLocale(UUID uuid);

    /**
     * Typically used internally.
     * This method is used to get the default player data object, e.g. if you attempt to use {@link #getLocale(UUID)} while the player is uncached, and will
     * default to the configured default locale.
     *
     * @return {@link PlayerData} default player data object. UUID: null / DISCORD_ID: none / LAST_RAID: 0 / LOCALE: default configured locale.
     */
    public PlayerData getDefaultPlayerData() {
        return new PlayerData("none", 0, Messages.getDefaulLocale());
    }

    /**
     * Convert an ItemStack to a skull with a specific texture.
     *
     * @param item Item to convert.
     * @param skullValue Skull texture value.
     *
     * @return {@link SkyItemStack} converted item.
     */
    public static SkyItemStack.SkyItemStackBuilder convertToSkull(SkyItemStack.SkyItemStackBuilder item, String skullValue) {
        item.textures(skullValue);
        /*item.editMeta(SkullMeta.class, skullMeta -> {
            final UUID uuid = UUID.randomUUID();
            final PlayerProfile playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
            playerProfile.setProperty(new ProfileProperty("textures", skullValue));

            skullMeta.setPlayerProfile(playerProfile);
        });*/

        return item;
    }

    /**
     * Used primarily in the paper implementation.
     * Use with PlaceholderAPI.
     *
     * @param user SkyUser object.
     * @param text Text to process.
     *
     * @return The processes text String.
     */
    public abstract String processText(SkyUser user, String text);

    /**
     * Clear a user's inventory.
     * Used on island delete.
     *
     * @param user SkyUser whose inventory should be wiped.
     */
    public abstract void clearInventory(SkyUser user);

    /**
     * Clear a user's ender chest contents.
     * Used on island delete.
     *
     * @param user SkyUser whose inventory should be wiped.
     */
    public abstract void clearEnderChest(SkyUser user);

    /**
     * Get all online players on the server.
     *
     * @return {@link List<SkyUser>} of all online players.
     */
    public abstract List<SkyUser> getOnlinePlayers();

    /**
     * Check if a user has inventory space.
     *
     * @param user SkyUser to check.
     *
     * @return true if they have space.
     */
    public abstract boolean hasInventorySpace(SkyUser user);
}
