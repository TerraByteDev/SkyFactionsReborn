package net.skullian.torrent.skyfactions.api;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.Runes;
import net.skullian.torrent.skyfactions.faction.Faction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunesAPI {

    /**
     * See whether an item is allowed for rune conversion.
     *
     * @param stack ItemStack in question/
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
            SkyFactionsReborn.db.addRunes(player, total);
            Messages.RUNE_CONVERSION_SUCCESS.send(player, "%added%", total);
        }
    }

    public static void handleRuneFactionConversion(List<ItemStack> stacks, Player player) {
        Faction faction = FactionAPI.getFaction(player);

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
            faction.addRunes(total);
            Messages.RUNE_CONVERSION_SUCCESS.send(player, "%added%", total);

        } else if (faction == null) {
            Messages.ERROR.send(player, "%operation%", "convert to runes", "%debug%", "SQL_UNKNOWN_FACTION");
            for (ItemStack stack : stacks) {
                if (stack != null && !stack.getType().equals(Material.AIR)) {
                    player.getInventory().addItem(stack);
                }
            }
        }
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
        } else if (!isBlacklist && !list.contains(stack.getType().name())){
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
