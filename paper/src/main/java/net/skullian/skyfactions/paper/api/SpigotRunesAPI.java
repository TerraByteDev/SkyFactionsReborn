package net.skullian.skyfactions.paper.api;

import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenItems;
import net.skullian.skyfactions.common.api.RunesAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.RunesConfig;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import net.skullian.skyfactions.paper.util.DependencyHandler;
import net.skullian.skyfactions.common.defence.struct.DefenceStruct;
import net.skullian.skyfactions.common.faction.Faction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("ConstantValue")
public class SpigotRunesAPI extends RunesAPI {

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isStackProhibited(SkyItemStack stack, SkyUser player) {
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
        } else if (DependencyHandler.isEnabled("ItemsAdder") && !RunesConfig.ALLOW_ITEMSADDER_ITEMS.getBoolean() && CustomStack.byItemStack(SpigotAdapter.adapt(stack, player, false)) != null) {
            return true;
        } else if (DependencyHandler.isEnabled("Oraxen") && !RunesConfig.ALLOW_ORAXEN_ITEMS.getBoolean() && OraxenItems.getIdByItem(SpigotAdapter.adapt(stack, player, false)) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void handlePlayerRuneConversion(List<SkyItemStack> stacks, SkyUser player) {
        if (!stacks.isEmpty()) {
            handleRuneConversion(stacks, player, null);
        }
    }

    @Override
    public void handleFactionRuneConversion(List<SkyItemStack> stacks, SkyUser player) {
        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "convert your items to runes", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction != null) {
                handleRuneConversion(stacks, player, faction);
            } else {
                Messages.ERROR.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "operation", "convert to runes", "debug", "SQL_UNKNOWN_FACTION");
                returnItems(stacks, player);
            }
        });
    }

    @Override
    public void handleRuneConversion(List<SkyItemStack> stacks, SkyUser player, Faction faction) {
        int total = 0;
        Map<String, Integer> overrides = RunesConfig.RUNE_OVERRIDES.getMap();

        Map<SkyItemStack, Integer> quantities = new HashMap<>();
        for (SkyItemStack skyItemStack : stacks) {
            ItemStack stack = ItemStack.deserializeBytes((byte[]) skyItemStack.getSerializedBytes());
            if (stack == null || stack.getType().equals(Material.AIR)) continue;
            quantities.put(skyItemStack, quantities.getOrDefault(skyItemStack, 0) + stack.getAmount());
        }

        List<SkyItemStack> remainingItems = new ArrayList<>();

        for (Map.Entry<SkyItemStack, Integer> entry : quantities.entrySet()) {
            SkyItemStack stack = entry.getKey();
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
                SkyItemStack cloned = stack.clone();
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
    public boolean hasEnchants(SkyItemStack stack) {
        return !SpigotAdapter.adapt(stack, null, false).getEnchantments().isEmpty();
    }

    @Override
    public boolean hasLore(SkyItemStack stack) {
        if (stack.getLore() != null) {
            return !stack.getLore().isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public boolean isAllowed(SkyItemStack skyItemStack) {
        ItemStack stack = ItemStack.deserializeBytes((byte[]) skyItemStack.getSerializedBytes());
        if (stack == null || stack.getType().equals(Material.AIR) || SkyApi.getInstance().getDefenceAPI().isDefence(skyItemStack)) return true;

        List<String> list = RunesConfig.MATERIALS_LIST.getList();
        boolean isBlacklist = RunesConfig.MATERIALS_IS_BLACKLIST.getBoolean();

        String identifier = getItemID(skyItemStack);

        if (isBlacklist && list.contains(identifier)) {
            return false;
        } else return isBlacklist || list.contains(identifier);
    }

    @Override
    public @NotNull String getItemID(SkyItemStack stack) {
        ItemStack itemStack = stack.getSerializedBytes() != null ? ItemStack.deserializeBytes((byte[]) stack.getSerializedBytes()) : SpigotAdapter.adapt(stack, null, false);
        if (DependencyHandler.isEnabled("ItemsAdder")) {

            CustomStack itemsAdderStack = CustomStack.byItemStack(itemStack);
            if (itemsAdderStack != null) return "ITEMSADDER:" + itemsAdderStack.getId();

        } else if (DependencyHandler.isEnabled("Oraxen")) {

            String oraxenID = OraxenItems.getIdByItem(itemStack);
            if (oraxenID != null) return "ORAXEN:" + oraxenID;

        }

        return itemStack.getType().name();
    }

    @Override
    public void returnItems(List<SkyItemStack> stacks, SkyUser player) {
        if (!stacks.isEmpty()) {
            for (SkyItemStack skyItemStack : stacks) {
                ItemStack stack = ItemStack.deserializeBytes((byte[]) skyItemStack.getSerializedBytes());
                if (stack == null || stack.getType().equals(Material.AIR)) return;

                ItemStack bukkitStack = SpigotAdapter.adapt(skyItemStack, player, false);
                Player bukkitPlayer = SpigotAdapter.adapt(player).getPlayer();
                if (bukkitPlayer == null) throw new NullPointerException("Adapted player is null!");
                Map<Integer, ItemStack> map = bukkitPlayer.getInventory().addItem(bukkitStack);

                // if the player's inventory is full, drop the item.
                for (ItemStack item : map.values()) {
                    bukkitPlayer.getWorld().dropItemNaturally(SpigotAdapter.adapt(player.getLocation()), item);
                }
            }
        }
    }
}
