package net.skullian.skyfactions.command.gems.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.HashMap;
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
        if (CommandsUtility.manageCooldown(player)) return;

        IslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, throwable) -> {
            if (throwable != null) {
                ErrorHandler.handleError(player, "get your island", "SQL_GEMS_GET", throwable);
                return;
            }

            if (hasIsland) {
                GemsAPI.getGems(player.getUniqueId()).whenComplete((gems, ex) -> {
                    if (ex != null) {
                        ErrorHandler.handleError(player, "get your gems balance", "SQL_GEMS_GET", ex);
                        return;
                    }
                    int parsedAmount;

                    if (amount.equalsIgnoreCase("all")) parsedAmount = gems;
                    else parsedAmount = Integer.parseInt(amount);

                    if (gems < parsedAmount) {
                        Messages.INSUFFICIENT_GEMS_COUNT.send(player);
                        return;
                    } else {
                        int available = parsedAmount - calculateAvailableInventorySpace(player);

                        ItemStack stack = GemsAPI.createGemsStack();
                        stack.setAmount(available);

                        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(stack);
                        int count = 0;
                        for (ItemStack leftoverStack : leftover.values()) {
                            count += leftoverStack.getAmount();
                        }

                        GemsAPI.subtractGems(player.getUniqueId(), parsedAmount - count).whenComplete((ignored, ex2) -> {
                            if (ex2 != null) {
                                for (int i = 0; i < player.getInventory().getSize(); i++) {
                                    if (GemsAPI.isGemsStack(player.getInventory().getItem(i))) {
                                        player.getInventory().setItem(i, null);
                                    }
                                }

                                ErrorHandler.handleError(player, "withdraw your gems", "SQL_GEMS_MODIFY", ex2);
                                return;
                            }

                            if (available < parsedAmount) {
                                Messages.GEMS_INSUFFICIENT_INVENTORY_SPACE.send(player);
                            } else {
                                Messages.GEMS_WITHDRAW_SUCCESS.send(player, "%amount%", available);
                            }
                        });
                    }
                });
            } else {
                Messages.NO_ISLAND.send(player);
            }
        });

    }

    private int calculateAvailableInventorySpace(Player player) {
        int capacity = 0;
        for (ItemStack item : player.getInventory()) {
            if (item == null || item.getType() == Material.AIR) {
                capacity += 64;
            } else if (GemsAPI.isGemsStack(item) && item.getAmount() < 64) {
                capacity += 64 - item.getAmount();
            }
        }

        return capacity;
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.gems.withdraw", "skyfactions.gems");
    }
}
