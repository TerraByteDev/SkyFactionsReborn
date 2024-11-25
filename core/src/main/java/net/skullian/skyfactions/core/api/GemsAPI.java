package net.skullian.skyfactions.core.api;

import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.config.types.Settings;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GemsAPI {

    public static final Map<UUID, Integer> playerGems = new ConcurrentHashMap<>();

    /**
     * Get a player's gem count.
     *
     * @param playerUUID UUID of the player you want to get gem count from.
     * @return {@link Integer}
     */
    public static CompletableFuture<Integer> getGems(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
             if (!playerGems.containsKey(playerUUID)) return cachePlayer(playerUUID).join();

             if (SkyFactionsReborn.getCacheService().getPlayersToCache().containsKey(playerUUID) && playerGems.containsKey(playerUUID)) return (playerGems.get(playerUUID) + SkyFactionsReborn.getCacheService().getPlayersToCache().get(playerUUID).getGems());
                else return playerGems.get(playerUUID);
        });
    }

    public static int getGemsIfCached(UUID playerUUID) {
        if (!playerGems.containsKey(playerUUID)) cachePlayer(playerUUID);

        if (SkyFactionsReborn.getCacheService().getPlayersToCache().containsKey(playerUUID) && playerGems.containsKey(playerUUID)) return (playerGems.get(playerUUID) + SkyFactionsReborn.getCacheService().getPlayersToCache().get(playerUUID).getGems());
            else return playerGems.get(playerUUID);
    }

    /**
     * Add gems to a player.
     *
     * @param playerUUID UUID of player to give gems to.
     * @param addition   Amount of gems to add.
     */
    public static void addGems(UUID playerUUID, int addition) {
        if (!playerGems.containsKey(playerUUID)) cachePlayer(playerUUID);

        SkyFactionsReborn.getCacheService().getEntry(playerUUID).addGems(addition);
    }

    /**
     * Subtract gems from a player.
     *
     * @param playerUUID  UUID of player to subtract gems from.
     * @param subtraction Amount of gems to remove.
     */
    public static void subtractGems(UUID playerUUID, int subtraction) {
        if (!playerGems.containsKey(playerUUID)) cachePlayer(playerUUID);

        SkyFactionsReborn.getCacheService().getEntry(playerUUID).removeGems(subtraction);
    }

    public static CompletableFuture<Integer> cachePlayer(UUID playerUUID) {
        return SkyFactionsReborn.getDatabaseManager().getCurrencyManager().getGems(playerUUID).whenComplete((gems, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else playerGems.put(playerUUID, gems);
        });
    }

    public static ItemStack createGemsStack(Player player) {
        ItemStack stack = new ItemStack(Material.valueOf(Settings.GEMS_MATERIAL.getString()));

        ItemMeta meta = stack.getItemMeta();
        if (Settings.GEMS_CUSTOM_MODEL_DATA.getInt() != -1) meta.setCustomModelData(Settings.GEMS_CUSTOM_MODEL_DATA.getInt());
        meta.displayName(TextUtility.color(Messages.GEMS_ITEM_NAME.getString(SpigotPlayerAPI.getLocale(player.getUniqueId())), SpigotPlayerAPI.getLocale(player.getUniqueId()), player));
        if (!Messages.GEMS_ITEM_LORE.getStringList(SpigotPlayerAPI.getLocale(player.getUniqueId())).isEmpty()) meta.lore(
                Messages.GEMS_ITEM_LORE.getStringList(SpigotPlayerAPI.getLocale(player.getUniqueId())).stream()
                        .map(text -> TextUtility.color(text, SpigotPlayerAPI.getLocale(player.getUniqueId()), player))
                        .collect(Collectors.toList())
        );

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(SkyFactionsReborn.getInstance(), "gem");
        container.set(key, PersistentDataType.BOOLEAN, true);

        stack.setItemMeta(meta);

        return stack;
    }

    public static boolean isGemsStack(ItemStack stack) {
        if (stack == null) return false;
        NamespacedKey key = new NamespacedKey(SkyFactionsReborn.getInstance(), "gem");
        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();

        return container.has(key, PersistentDataType.BOOLEAN);
    }
}
