package net.skullian.torrent.skyfactions.api;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.config.types.Runes;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RunesAPI {

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
        int total = 0;

        if (!stacks.isEmpty()) {
            Map<String, Integer> overrides = Runes.RUNE_OVERRIDES.getMap();
            int forEach = Runes.BASE_FOR_EACH.getInt();
            int returnForEach = Runes.BASE_RUNE_RETURN.getInt();

            List<ItemStack> remaindingItems = new ArrayList<>();

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
                        remaindingItems.add(cloned);
                    }
                }
            }
            returnItems(remaindingItems, player);
            if (total > 0) {
                SkyFactionsReborn.db.addRunes(player.getUniqueId(), total);
                Messages.RUNE_CONVERSION_SUCCESS.send(player, "%added%", total);
            } else {
                Messages.RUNE_INSUFFICIENT_ITEMS.send(player);
            }
        }
    }

    public static void handleRuneFactionConversion(List<ItemStack> stacks, Player player) {
        FactionAPI.getFaction(player).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "convert your items to runes", "SQL_FACTION_GET", ex);
                return;
            }

            int total = 0;
            if (faction != null) {
                Map<String, Integer> overrides = Runes.RUNE_OVERRIDES.getMap();
                int forEach = Runes.BASE_FOR_EACH.getInt();
                int returnForEach = Runes.BASE_RUNE_RETURN.getInt();

                List<ItemStack> remaindingItems = new ArrayList<>();

                for (ItemStack stack : stacks) {
                    if (stack == null || stack.getType().equals(Material.AIR)) continue;
                    if (overrides.containsKey(stack.getType().name())) {
                        int amount = overrides.get(stack.getType().name()) * stack.getAmount();
                        total += amount;
                    } else {
                        int quotient = stack.getAmount() / forEach;
                        int remainder = stack.getAmount() % forEach;
                        remainder = remainder > 0 ? remainder + 1 : 0;

                        int amount = quotient * returnForEach;
                        total += amount;

                        ItemStack cloned = stack.clone();
                        cloned.setAmount(remainder);
                        remaindingItems.add(cloned);
                    }
                }

                returnItems(remaindingItems, player);
                if (total > 0) {
                    faction.addRunes(total);
                    Messages.RUNE_CONVERSION_SUCCESS.send(player, "%added%", total);
                } else {
                    Messages.RUNE_INSUFFICIENT_ITEMS.send(player);
                }

            } else if (faction == null) {
                Messages.ERROR.send(player, "%operation%", "convert to runes", "%debug%", "SQL_UNKNOWN_FACTION");
                for (ItemStack stack : stacks) {
                    if (stack != null && !stack.getType().equals(Material.AIR)) {
                        player.getInventory().addItem(stack);
                    }
                }
            }
        });
    }

    /**
     * Remove runes from a player.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @param amount     Amount of runes to remove {@link Integer}
     * @return {@link CompletableFuture<Void>}
     */
    public static CompletableFuture<Void> removeRunes(UUID playerUUID, int amount) {
        return SkyFactionsReborn.db.removeRunes(playerUUID, amount);
    }

    /**
     * Add runes to a player's rune balance.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @param amount     Amount of runes to add {@link Integer}
     * @return {@link CompletableFuture<Void>}
     */
    public static CompletableFuture<Void> addRunes(UUID playerUUID, int amount) {
        return SkyFactionsReborn.db.addRunes(playerUUID, amount);
    }

    /**
     * Get a player's rune count.
     *
     * @param playerUUID UUID of the Player {@link UUID}
     * @return {@link CompletableFuture<Integer>}
     */
    public static CompletableFuture<Integer> getRunes(UUID playerUUID) {
        return SkyFactionsReborn.db.getRunes(playerUUID);
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

                player.getInventory().addItem(stack);
            }
        }
    }
}
