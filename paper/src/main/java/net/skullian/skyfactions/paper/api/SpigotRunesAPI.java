package net.skullian.skyfactions.paper.api;

import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenItems;
import net.skullian.skyfactions.common.api.RunesAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.RunesConfig;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotRunesAPI extends RunesAPI {

    public final Map<UUID, Integer> playerRunes = new ConcurrentHashMap<>();
    
    @Override
    public boolean isStackProhibited(ItemStack stack, Player player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        
        if (!RunesConfig.ALLOW_ENCHANTS.getBoolean() && hasEnchants(stack)) {
            Messages.RUNE_ENCHANT_DENY.send(player, locale);
            return true;
        } else if (!RunesConfig.ALLOW_LORE.getBoolean() && hasLore(stack)) {
            Messages.RUNE_GENERAL_DENY.send(player, locale);
            return true;
        } else if (!isAllowed(stack)) {
            Messages.RUNE_GENERAL_DENY.send(player, locale);
            return true;
        } else if (DependencyHandler.isEnabled("ItemsAdder") && !RunesConfig.ALLOW_ITEMSADDER_ITEMS.getBoolean() && CustomStack.byItemStack(stack) != null) {
            return true;
        } else if (DependencyHandler.isEnabled("Oraxen") && !RunesConfig.ALLOW_ORAXEN_ITEMS.getBoolean() && OraxenItems.getIdByItem(stack) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void handlePlayerRuneConversion(List<ItemStack> stacks, Player player) {
        if (!stacks.isEmpty()) {
            handleRuneConversion(stacks, player, null);
        }
    }

    @Override
    public void handleFactionRuneConversion(List<ItemStack> stacks, Player player) {
        SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "convert your items to runes", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                handleRuneConversion(stacks, player, faction);
            } else {
                Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "convert to runes", "debug", "SQL_UNKNOWN_FACTION");
                for (ItemStack stack : stacks) {
                    if (stack != null && !stack.getType().equals(Material.AIR)) {
                        player.getInventory().addItem(stack);
                    }
                }
            }
        });
    }

    @Override
    public void handleRuneConversion(List<ItemStack> stacks, Player player, Faction faction) {
        int total = 0;
        Map<String, Integer> overrides = RunesConfig.RUNE_OVERRIDES.getMap();

        Map<ItemStack, Integer> quantities = new HashMap<>();
        for (ItemStack stack : stacks) {
            if (stack == null || stack.getType().equals(Material.AIR)) continue;
            quantities.put(stack, quantities.getOrDefault(stack, 0) + stack.getAmount());
        }

        List<ItemStack> remainingItems = new ArrayList<>();

        for (Map.Entry<ItemStack, Integer> entry : quantities.entrySet()) {
            ItemStack stack = entry.getKey();
            DefenceStruct struct = SkyApi.getInstance().getDefenceAPI().getDefenceFromItem(stack, player);
            if (struct != null) {
                total += struct.getSELL_COST();
            }

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

            Messages.RUNE_CONVERSION_SUCCESS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "added", total);
        } else {
            Messages.RUNE_INSUFFICIENT_ITEMS.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
        }
    }

    @Override
    public void removeRunes(UUID playerUUID, int amount) {
        if (!SkyApi.getInstance().getUserManager().isCached(playerUUID)) SkyApi.getInstance().getUserManager().getUser(playerUUID);

        SkyApi.getInstance().getCacheService().getEntry(playerUUID).removeRunes(amount);
    }

    @Override
    public void addRunes(UUID playerUUID, int amount) {
        if (!SkyApi.getInstance().getUserManager().isCached(playerUUID)) SkyApi.getInstance().getUserManager().getUser(playerUUID);

        SkyApi.getInstance().getCacheService().getEntry(playerUUID).addRunes(amount);
    }

    @Override
    public boolean hasEnchants(ItemStack stack) {
        return stack.getEnchantments().size() > 0;
    }

    @Override
    public boolean hasLore(ItemStack stack) {
        if (stack.lore() != null) {
            return stack.lore().size() > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAllowed(ItemStack stack) {
        if (stack == null || stack.getType().equals(Material.AIR) || SkyApi.getInstance().getDefenceAPI().isDefence(stack)) return true;

        List<String> list = RunesConfig.MATERIALS_LIST.getList();
        boolean isBlacklist = RunesConfig.MATERIALS_IS_BLACKLIST.getBoolean();

        String identifier = getItemID(stack);

        if (isBlacklist && list.contains(identifier)) {
            return false;
        } else return isBlacklist || list.contains(identifier);
    }

    @Override
    public @NotNull String getItemID(ItemStack stack) {
        if (DependencyHandler.isEnabled("ItemsAdder")) {

            CustomStack itemsAdderStack = CustomStack.byItemStack(stack);
            if (itemsAdderStack != null) return "ITEMSADDER:" + itemsAdderStack.getId();

        } else if (DependencyHandler.isEnabled("Oraxen")) {

            String oraxenID = OraxenItems.getIdByItem(stack);
            if (oraxenID != null) return "ORAXEN:" + oraxenID;

        }

        return stack.getType().name();
    }

    @Override
    public void returnItems(List<ItemStack> stacks, Player player) {
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
