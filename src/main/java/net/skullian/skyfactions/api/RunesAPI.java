package net.skullian.skyfactions.api;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Runes;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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
        if (!Runes.ALLOW_ENCHANTS.getBoolean() && hasEnchants(stack)) {
            Messages.RUNE_ENCHANT_DENY.send(player);
            return true;
        } else if (!Runes.ALLOW_LORE.getBoolean() && hasLore(stack)) {
            Messages.RUNE_GENERAL_DENY.send(player);
            return true;
        } else if (!isAllowed(stack)) {
            Messages.RUNE_GENERAL_DENY.send(player);
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
                ErrorHandler.handleError(player, "convert your items to runes", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                handleConversion(stacks, player, faction);
            } else {
                Messages.ERROR.send(player, "%operation%", "convert to runes", "%debug%", "SQL_UNKNOWN_FACTION");
                for (ItemStack stack : stacks) {
                    if (stack != null && !stack.getType().equals(Material.AIR)) {
                        player.getInventory().addItem(stack);
                    }
                }
            }
        });
    }

    public static void handleConversion(List<ItemStack> stacks, Player player, Faction faction) {
        int total = 0;
        Map<String, Integer> overrides = Runes.RUNE_OVERRIDES.getMap();
        int forEach = Runes.BASE_FOR_EACH.getInt();
        int returnForEach = Runes.BASE_RUNE_RETURN.getInt();

        List<ItemStack> remainingItems = new ArrayList<>();

        for (ItemStack stack : stacks) {
            if (stack == null || stack.getType().equals(Material.AIR)) continue;
            if (overrides.containsKey(stack.getType().name())) {
                int amount = overrides.get(stack.getType().name()) * stack.getAmount();
                total += amount;
            } else {
                int quotient = stack.getAmount() / forEach;
                int remainder = stack.getAmount() % forEach;

                int amount = quotient * returnForEach;
                total += amount;

                if (remainder > 0) {
                    ItemStack cloned = stack.clone();
                    cloned.setAmount(remainder);
                    remainingItems.add(cloned);
                }
            }
        }

        returnItems(remainingItems, player);
        if (total > 0) {
            if (faction != null) {
                faction.addRunes(total);
            } else {
                addRunes(player.getUniqueId(), total);
            }

            Messages.RUNE_CONVERSION_SUCCESS.send(player, "%added%", total);
        } else {
            Messages.RUNE_INSUFFICIENT_ITEMS.send(player);
        }
    }

    /**
     * Remove runes from a player.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @param amount     Amount of runes to remove {@link Integer}
     * @return {@link CompletableFuture<Void>}
     */
    public static CompletableFuture<Void> removeRunes(UUID playerUUID, int amount) {
        playerRunes.replace(playerUUID, playerRunes.get(playerUUID) - amount);
        return SkyFactionsReborn.databaseHandler.removeRunes(playerUUID, amount).exceptionally(ex -> {
            playerRunes.replace(playerUUID, playerRunes.get(playerUUID) + amount);
            return null;
        });
    }

    /**
     * Add runes to a player's rune balance.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @param amount     Amount of runes to add {@link Integer}
     * @return {@link CompletableFuture<Void>}
     */
    public static CompletableFuture<Void> addRunes(UUID playerUUID, int amount) {
        playerRunes.replace(playerUUID, playerRunes.get(playerUUID) + amount);
        return SkyFactionsReborn.databaseHandler.addRunes(playerUUID, amount).exceptionally(ex -> {
            playerRunes.replace(playerUUID, playerRunes.get(playerUUID) - amount);
            return null;
        });
    }

    /**
     * Get a player's rune count.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @return {@link CompletableFuture<Integer>}
     */
    public static CompletableFuture<Integer> getRunes(UUID playerUUID) {
        if (playerRunes.containsKey(playerUUID)) return CompletableFuture.completedFuture(playerRunes.get(playerUUID));
        return SkyFactionsReborn.databaseHandler.getRunes(playerUUID).whenComplete((runes, ex) -> {
            if (ex != null) return;

            playerRunes.put(playerUUID, runes);
        });
    }

    private static int getDefenceCost(ItemStack item) {
        NamespacedKey defenceKey = new NamespacedKey(SkyFactionsReborn.getInstance(), "defence-identifier");
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

        if (container.has(defenceKey, PersistentDataType.STRING)) {
            String identifier = container.get(defenceKey, PersistentDataType.STRING);
            DefenceStruct struct = DefencesFactory.defences.get(identifier);
            if (struct != null) return struct.getSELL_COST();
        }

        return -1;
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

        List<String> list = Runes.MATERIALS_LIST.getList();
        boolean isBlacklist = Runes.MATERIALS_IS_BLACKLIST.getBoolean();

        if (isBlacklist && list.contains(stack.getType().name())) {
            return false;
        } else if (!isBlacklist && !list.contains(stack.getType().name())) {
            return false;
        } else {
            return true;
        }
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
