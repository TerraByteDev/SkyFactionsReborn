package net.skullian.skyfactions.paper.api;

import net.skullian.skyfactions.common.api.GemsAPI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.paper.PaperSharedConstants;
import net.skullian.skyfactions.paper.api.adapter.SpigotAdapter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class SpigotGemsAPI extends GemsAPI {

    public boolean isGemsStack(ItemStack stack) {
        if (stack == null) return false;
        PersistentDataContainer container = stack.getItemMeta().getPersistentDataContainer();

        return container.has(PaperSharedConstants.GEM_IDENTIFIER_KEY, PersistentDataType.BOOLEAN);
    }

    @Override
    public int depositAllItems(SkyUser player, SkyItemStack currencyItem) {
        if (!player.isOnline()) return 0;
        Player spigotPlayer = SpigotAdapter.adapt(player).getPlayer();
        if (spigotPlayer == null) throw new NullPointerException("Adapted player is null!");
        ItemStack spigotCurrencyItem = SpigotAdapter.adapt(currencyItem, player, false);

        Inventory inventory = spigotPlayer.getInventory();
        int totalDeposited = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slotContents = inventory.getItem(i);
            if (slotContents != null && slotContents.isSimilar(spigotCurrencyItem)) {
                int amountInSlot = slotContents.getAmount();
                slotContents.setAmount(0);
                totalDeposited += amountInSlot;
            }
        }

        spigotPlayer.updateInventory();
        return totalDeposited;
    }

    @Override
    public int depositSpecificAmount(SkyUser player, SkyItemStack currencyItem, int amount) {
        if (!player.isOnline()) return 0;
        Player spigotPlayer = SpigotAdapter.adapt(player).getPlayer();
        if (spigotPlayer == null) throw new NullPointerException("Adapted player is null!");
        ItemStack spigotCurrencyItem = SpigotAdapter.adapt(currencyItem, player, false);

        Inventory inventory = spigotPlayer.getInventory();
        int totalDeposited = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slotContents = inventory.getItem(i);
            if (slotContents != null && slotContents.isSimilar(spigotCurrencyItem)) {
                int amountInSlot = Math.min(amount, slotContents.getAmount());
                slotContents.setAmount(slotContents.getAmount() - amountInSlot);
                totalDeposited += amountInSlot;

                if (totalDeposited >= amount) break;
            }
        }

        spigotPlayer.updateInventory();
        return totalDeposited;
    }

    @Override
    public int addItemToInventory(SkyUser user, SkyItemStack stack) {
        if (!user.isOnline()) return 0;
        Player player = SpigotAdapter.adapt(user).getPlayer();
        if (player == null) throw new NullPointerException("Adapted player is null!");
        Inventory inventory = player.getInventory();

        ItemStack itemStack = SpigotAdapter.adapt(stack, user, false);
        int remaining = itemStack.getAmount();
        int[] blockedSlots = new int[]{103, 102, 101, 100}; // armor slots

        while (remaining > 0) {
            boolean added = false;

            for (int i = 0; i < inventory.getSize(); i++) {
                final int index = i;
                if (Arrays.stream(blockedSlots).anyMatch(x -> x == index)) continue;

                ItemStack slot = inventory.getItem(i);

                if (slot == null || slot.getType() == Material.AIR) {
                    int space = Math.min(remaining, 64);
                    inventory.setItem(i, cloneItem(itemStack, space));
                    remaining -= space;

                    if (remaining == 0) {
                        return 0;
                    }

                    added = true;
                    break;
                } else if (slot.isSimilar(itemStack)) {
                    int maxStackSize = Math.min(slot.getMaxStackSize(), inventory.getMaxStackSize());
                    int space = maxStackSize - slot.getAmount();

                    if (space > 0) {
                        int addAmount = Math.min(space, remaining);

                        ItemStack newSlot = cloneItem(slot, slot.getAmount() + addAmount);
                        inventory.setItem(i, newSlot);
                        remaining -= addAmount;

                        if (remaining == 0) {
                            return 0;
                        }

                        added = true;
                        break;
                    }
                }
            }

            if (!added) {
                return remaining;
            }
        }

        return 0;
    }

    private ItemStack cloneItem(ItemStack original, int amount) {
        ItemStack cloned = original.clone();
        cloned.setAmount(Math.min(amount, 64));
        return cloned;
    }
}
