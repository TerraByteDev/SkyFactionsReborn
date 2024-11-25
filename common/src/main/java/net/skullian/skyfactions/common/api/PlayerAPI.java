package net.skullian.skyfactions.common.api;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

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
     * @return {@link PlayerData} default player data object. UUID: null / DISCORD_ID: null / LAST_RAID: 0 / LOCALE: default configured locale.
     */
    public PlayerData getDefaultPlayerData() {
        return new PlayerData(null, null, 0, Messages.getDefaulLocale());
    }

    /**
     * Convert an ItemStack to a skull with a specific texture.
     *
     * @param item Item to convert.
     * @param skullValue Skull texture value.
     *
     * @return {@link ItemStack} converted item.
     */
    public static ItemStack convertToSkull(ItemStack item, String skullValue) {
        if (item.getType() == Material.PLAYER_HEAD) {
            item.editMeta(SkullMeta.class, skullMeta -> {
                final UUID uuid = UUID.randomUUID();
                final PlayerProfile playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
                playerProfile.setProperty(new ProfileProperty("textures", skullValue));

                skullMeta.setPlayerProfile(playerProfile);
            });
        }

        return item;
    }

    /**
     * Used for the universal player_skull placeholder in GUIs.
     * Fetches the player's skull and sets it to the item.
     *
     * @param item ItemStack to modify.
     * @param playerUUID Player UUID to fetch the skull of.
     *
     * @return {@link ItemStack} - Adapted ItemStack.
     */
    public static ItemStack getPlayerSkull(ItemStack item, UUID playerUUID) {
        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
            item.setItemMeta(meta);
        }

        return item;
    }
}
