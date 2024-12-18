package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.List;

@Command("gems")
public class GemsDepositCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "deposit";
    }

    @Override
    public String getDescription() {
        return "Deposit any gems in item form from your inventory.";
    }

    @Override
    public String getSyntax() {
        return "/gems deposit <amount / all>";
    }

    @Command("deposit <amount>")
    public void run(
            Player player,
            @Argument(value = "amount") String amount
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        IslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", throwable);
                return;
            } else if (!hasIsland) {
                Messages.NO_ISLAND.send(player, PlayerAPI.getLocale(player.getUniqueId()));
            }

            ItemStack currencyItem = GemsAPI.createGemsStack(player);
            int totalDeposited = 0;

            if (amount.equalsIgnoreCase("all")) {
                totalDeposited = depositAllItems(player, currencyItem);
            } else {
                try {
                    int parsedAmount = Integer.parseInt(amount);
                    totalDeposited = depositSpecificAmount(player, currencyItem, parsedAmount);
                } catch (NumberFormatException exception) {
                    Messages.INCORRECT_USAGE.send(player, PlayerAPI.getLocale(player.getUniqueId()), "usage", getSyntax());
                }
            }

            int finalTotalDeposited = totalDeposited;
            GemsAPI.subtractGems(player.getUniqueId(), totalDeposited);
            Messages.GEMS_DEPOSIT_SUCCESS.send(player, PlayerAPI.getLocale(player.getUniqueId()), "amount", finalTotalDeposited);
        });

    }

    private int depositAllItems(Player player, ItemStack currencyItem) {
        Inventory inventory = player.getInventory();
        int totalDeposited = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slotContents = inventory.getItem(i);
            if (slotContents != null && slotContents.isSimilar(currencyItem)) {
                int amountInSlot = slotContents.getAmount();
                slotContents.setAmount(0);
                totalDeposited += amountInSlot;
            }
        }

        player.updateInventory();
        return totalDeposited;
    }

    private int depositSpecificAmount(Player player, ItemStack currencyItem, int amount) {
        Inventory inventory = player.getInventory();
        int totalDeposited = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slotContents = inventory.getItem(i);
            if (slotContents != null && slotContents.isSimilar(currencyItem)) {
                int amountInSlot = Math.min(amount, slotContents.getAmount());
                slotContents.setAmount(slotContents.getAmount() - amountInSlot);
                totalDeposited += amountInSlot;

                if (totalDeposited >= amount) break;
            }
        }

        player.updateInventory();
        return totalDeposited;
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.gems.deposit", "skyfactions.gems");
    }
}
