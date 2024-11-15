package net.skullian.skyfactions.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenItems;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.DependencyHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.RunesConfig;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.util.ErrorUtil;

public class RunesAPI {

    public static final Map<UUID, Integer> playerRunes = new HashMap<>();

    /**
     * See whether an item is allowed for rune conversion.
     *
     * @param stack  ItemStack in question/
     * @param player Player related to the ItemStack.
     * @return {@link Boolean}
     */
    public static boolean isStackProhibited(ItemStack stack, Player player) {
        if (!RunesConfig.ALLOW_ENCHANTS.getBoolean() && hasEnchants(stack)) {
            Messages.RUNE_ENCHANT_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            return true;
        } else if (!RunesConfig.ALLOW_LORE.getBoolean() && hasLore(stack)) {
            Messages.RUNE_GENERAL_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            return true;
        } else if (!isAllowed(stack)) {
            Messages.RUNE_GENERAL_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            return true;
        } else if (DependencyHandler.isEnabled("ItemsAdder") && !RunesConfig.ALLOW_ITEMSADDER_ITEMS.getBoolean() && CustomStack.byItemStack(stack) != null) {
            return true;
        } else if (DependencyHandler.isEnabled("Oraxen") && !RunesConfig.ALLOW_ORAXEN_ITEMS.getBoolean() && OraxenItems.getIdByItem(stack) != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void handleRuneConversion(List<ItemStack> stacks, Player player) {
        if (!stacks.isEmpty()) {
            handleConversion(stacks, player, null);
        }
    }

    public static void handleRuneFactionConversion(List<ItemStack> stacks, Player player) {
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "convert your items to runes", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                handleConversion(stacks, player, faction);
            } else {
                Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "convert to runes", "debug", "SQL_UNKNOWN_FACTION");
                for (ItemStack stack : stacks) {
                    if (stack != null && !stack.getType().equals(Material.AIR)) {
                        player.getInventory().addItem(stack);
                    }
                }
            }
        });
    }

    public static void handleConversion(List<ItemStack> stacks, Player player, Faction faction) {
        int total = getDefenceCost(stacks, player);
        Map<String, Integer> overrides = RunesConfig.RUNE_OVERRIDES.getMap();

        Map<ItemStack, Integer> quantities = new HashMap<>();
        for (ItemStack stack : stacks) {
            if (stack == null || stack.getType().equals(Material.AIR)) continue;
            quantities.put(stack, quantities.getOrDefault(stack, 0) + stack.getAmount());
        }

        List<ItemStack> remainingItems = new ArrayList<>();

        for (Map.Entry<ItemStack, Integer> entry : quantities.entrySet()) {
            ItemStack stack = entry.getKey();
            int quantity = entry.getValue();

            int tempForEach = RunesConfig.BASE_FOR_EACH.getInt();
            int tempReturn = RunesConfig.BASE_RUNE_RETURN.getInt();

            String id = getItemID(stack);
            if (overrides.containsKey(id)) {
                tempReturn = overrides.get(id);
                tempForEach = 1;
            }

            int quotient = quantity / tempForEach;
            int remainder = quantity % tempForEach;

            int amount = quotient * tempReturn;
            total += amount;

            if (remainder > 0) {
                ItemStack cloned = stack.clone();
                cloned.setAmount(remainder);
                remainingItems.add(cloned);
            }
        }

        returnItems(remainingItems, player);
        if (total > 0) {
            if (faction != null) {
                faction.addRunes(total);
            } else {
                addRunes(player.getUniqueId(), total);
            }

            Messages.RUNE_CONVERSION_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "added", total);
        } else {
            Messages.RUNE_INSUFFICIENT_ITEMS.send(player, PlayerHandler.getLocale(player.getUniqueId()));
        }
    }

    /**
     * Remove runes from a player.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @param amount     Amount of runes to remove {@link Integer}
     */
    public static void removeRunes(UUID playerUUID, int amount) {
        if (!playerRunes.containsKey(playerUUID)) cachePlayer(playerUUID);

        SkyFactionsReborn.cacheService.subtractRunes(playerUUID, amount);
    }

    /**
     * Add runes to a player's rune balance.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @param amount     Amount of runes to add {@link Integer}
     */
    public static void addRunes(UUID playerUUID, int amount) {
        if (!playerRunes.containsKey(playerUUID)) cachePlayer(playerUUID);

        SkyFactionsReborn.cacheService.addRunes(playerUUID, amount);
    }

    /**
     * Get a player's rune count.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @return {@link Integer}
     */
    public static int getRunes(UUID playerUUID) {
        if (!playerRunes.containsKey(playerUUID)) cachePlayer(playerUUID);

        if (SkyFactionsReborn.cacheService.playersToCache.containsKey(playerUUID)) return (playerRunes.get(playerUUID) + SkyFactionsReborn.cacheService.playersToCache.get(playerUUID).getRunes());
            else return playerRunes.get(playerUUID);
    }

    public static void cachePlayer(UUID playerUUID) {
        SkyFactionsReborn.databaseManager.currencyManager.getRunes(playerUUID).whenComplete((runes, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            playerRunes.put(playerUUID, runes);
        });
    }

    private static int getDefenceCost(List<ItemStack> items, Player player) {
        int count = 0;

        for (ItemStack item : items) {
            if (item == null || item.getType().equals(Material.AIR)) continue;

            NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

            if (container.has(defenceKey, PersistentDataType.STRING)) {
                String identifier = container.get(defenceKey, PersistentDataType.STRING);
                DefenceStruct struct = DefencesFactory.defences.getOrDefault(PlayerHandler.getLocale(player.getUniqueId()), DefencesFactory.getDefaultStruct()).get(identifier);
                if (struct != null) count += struct.getSELL_COST();
            }
        }

        return count;
    }

    private static boolean hasEnchants(ItemStack stack) {
        return stack.getEnchantments().size() > 0;
    }

    private static boolean hasLore(ItemStack stack) {
        if (stack.lore() != null) {
            return stack.lore().size() > 0;
        } else {
            return false;
        }
    }

    private static boolean isAllowed(ItemStack stack) {
        if (stack == null || stack.getType().equals(Material.AIR) || DefenceAPI.isDefence(stack)) return true;
        if (DefenceAPI.isDefence(stack)) return true;

        List<String> list = RunesConfig.MATERIALS_LIST.getList();
        boolean isBlacklist = RunesConfig.MATERIALS_IS_BLACKLIST.getBoolean();

        String identifier = getItemID(stack);

        if (isBlacklist && list.contains(identifier)) {
            return false;
        } else return isBlacklist || list.contains(identifier);
    }

    private static String getItemID(ItemStack stack) {
        if (DependencyHandler.isEnabled("ItemsAdder")) {

            CustomStack itemsAdderStack = CustomStack.byItemStack(stack);
            if (itemsAdderStack != null) return "ITEMSADDER:" + itemsAdderStack.getId();

        } else if (DependencyHandler.isEnabled("Oraxen")) {

            String oraxenID = OraxenItems.getIdByItem(stack);
            if (oraxenID != null) return oraxenID;

        }

        return stack.getType().name();
    }

    private static void returnItems(List<ItemStack> stacks, Player player) {
        if (!stacks.isEmpty()) {
            for (ItemStack stack : stacks) {
                if (stack == null || stack.getType().equals(Material.AIR)) return;

                Map<Integer, ItemStack> map = player.getInventory().addItem(stack);

                // if the player's inventory is full, drop the item.
                for (ItemStack item : map.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
        }
    }
}
