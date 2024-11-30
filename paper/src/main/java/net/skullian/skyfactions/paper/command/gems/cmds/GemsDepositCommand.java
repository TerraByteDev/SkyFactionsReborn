package net.skullian.skyfactions.paper.command.gems.cmds;

import net.skullian.skyfactions.paper.api.SpigotGemsAPI;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
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

        SpigotIslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", throwable);
                return;
            } else if (!hasIsland) {
                Messages.NO_ISLAND.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
            }

            ItemStack currencyItem = SpigotGemsAPI.createGemsStack(player);
            int totalDeposited = 0;

            if (amount.equalsIgnoreCase("all")) {
                totalDeposited = depositAllItems(player, currencyItem);
            } else {
                try {
                    int parsedAmount = Integer.parseInt(amount);
                    totalDeposited = depositSpecificAmount(player, currencyItem, parsedAmount);
                } catch (NumberFormatException exception) {
                    Messages.INCORRECT_USAGE.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "usage", getSyntax());
                }
            }

            int finalTotalDeposited = totalDeposited;
            SpigotGemsAPI.subtractGems(player.getUniqueId(), totalDeposited);
            Messages.GEMS_DEPOSIT_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "amount", finalTotalDeposited);
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
