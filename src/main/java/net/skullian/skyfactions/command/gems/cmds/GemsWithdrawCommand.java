package net.skullian.skyfactions.command.gems.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.Arrays;
import java.util.List;

@Command("gems")
public class GemsWithdrawCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "withdraw";
    }

    @Override
    public String getDescription() {
        return "Withdraw some / all of your gems.";
    }

    @Override
    public String getSyntax() {
        return "/gems withdraw <amount / all>";
    }

    @Command("withdraw <amount>")
    public void run(
            CommandSourceStack commandSourceStack,
            @Argument(value = "amount") String amount
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        IslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_GEMS_GET", throwable);
                return;
            }

            if (hasIsland) {
                int gems = GemsAPI.getGems(player.getUniqueId());
                try {
                    int parsedAmount = amount.equalsIgnoreCase("all") ? gems : (Integer.parseInt(amount) > gems ? gems : Integer.parseInt(amount));
                    ItemStack stack = GemsAPI.createGemsStack(player);
                    stack.setAmount(parsedAmount);

                    int remainingItems = addItemToInventory(player.getInventory(), stack);

                    GemsAPI.subtractGems(player.getUniqueId(), (parsedAmount - remainingItems));

                    Messages.GEMS_WITHDRAW_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "amount", parsedAmount);
                    if (remainingItems > 0) {
                        Messages.GEMS_INSUFFICIENT_INVENTORY_SPACE.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                    }
                } catch (NumberFormatException exception) {
                    Messages.INCORRECT_USAGE.send(player, PlayerHandler.getLocale(player.getUniqueId()), "usage", getSyntax());
                }
            } else {
                Messages.NO_ISLAND.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            }
        });

    }

    private int addItemToInventory(Inventory inventory, ItemStack itemStack) {
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

                    if (remaining <= 0) {
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

                        if (remaining <= 0) {
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

    @Override
    public List<String> permission() {
        return List.of("skyfactions.gems.withdraw", "skyfactions.gems");
    }
}
