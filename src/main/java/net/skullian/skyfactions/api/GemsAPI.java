package net.skullian.skyfactions.api;

import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class GemsAPI {

    public static final Map<UUID, Integer> playerGems = new HashMap<>();

    /**
     * Get a player's gem count.
     *
     * @param playerUUID UUID of the player you want to get gem count from.
     * @return {@link Integer}
     */
    public static int getGems(UUID playerUUID) {
        if (!playerGems.containsKey(playerUUID)) cachePlayer(playerUUID);

        if (SkyFactionsReborn.cacheService.playersToCache.containsKey(playerUUID)) return (playerGems.get(playerUUID) + SkyFactionsReborn.cacheService.playersToCache.get(playerUUID).getGems());
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

        SkyFactionsReborn.cacheService.addGems(playerUUID, addition);
    }

    /**
     * Subtract gems from a player.
     *
     * @param playerUUID  UUID of player to subtract gems from.
     * @param subtraction Amount of gems to remove.
     */
    public static void subtractGems(UUID playerUUID, int subtraction) {
        if (!playerGems.containsKey(playerUUID)) cachePlayer(playerUUID);

        SkyFactionsReborn.cacheService.subtractGems(playerUUID, subtraction);
    }

    public static void cachePlayer(UUID playerUUID) {
        SkyFactionsReborn.databaseHandler.getGems(playerUUID).whenComplete((gems, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            playerGems.put(playerUUID, gems);
        });
    }

    public static ItemStack createGemsStack() {
        ItemStack stack = new ItemStack(Material.valueOf(Settings.GEMS_MATERIAL.getString()));

        ItemMeta meta = stack.getItemMeta();
        if (Settings.GEMS_CUSTOM_MODEL_DATA.getInt() != -1) meta.setCustomModelData(Settings.GEMS_CUSTOM_MODEL_DATA.getInt());
        meta.setDisplayName(TextUtility.color(Settings.GEMS_ITEM_NAME.getString()));
        if (!Settings.GEMS_ITEM_LORE.getList().isEmpty()) meta.setLore(
                Settings.GEMS_ITEM_LORE.getList().stream()
                        .map(TextUtility::color)
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
