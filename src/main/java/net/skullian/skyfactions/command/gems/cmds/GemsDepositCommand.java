package net.skullian.skyfactions.command.gems.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.GemsAPI;
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
import java.util.Map;

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
            CommandSourceStack commandSourceStack,
            @Argument(value = "amount") String amount
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        Map<Integer, ItemStack> stacks = calculateGemsPresent(player);
        int presentGems = computeGemsPresentFromMap(stacks);
        if (presentGems == 0) {
            Messages.NO_GEMS_PRESENT.send(player);
            return;
        }

        int parsedAmount;
        if (amount.equalsIgnoreCase("all")) parsedAmount = presentGems;
        else parsedAmount = (Integer.parseInt(amount) - presentGems);

        int computedAmount = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (GemsAPI.isGemsStack(player.getInventory().getItem(i))) {
                computedAmount += player.getInventory().getItem(i).getAmount();

                player.getInventory().setItem(i, null);
            }
        }

        GemsAPI.addGems(player.getUniqueId(), computedAmount).whenComplete((ignored, exc) -> {
            if (exc != null) {
                ErrorHandler.handleError(player, "deposit your gems", "SQL_GEMS_MODIFY", exc);
                return;
            }

            Messages.GEMS_DEPOSIT_SUCCESS.send(player, "%amount%", "GEMS_DEPOSIT_SUCCESS");
        });

    }

    private Map<Integer, ItemStack> calculateGemsPresent(Player player) {
        Map<Integer, ItemStack> stacks = new HashMap<>();

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (GemsAPI.isGemsStack(player.getInventory().getItem(i))) {
                stacks.put(i, player.getInventory().getItem(i));
            }
        }

        return stacks;
    }

    private int computeGemsPresentFromMap(Map<Integer, ItemStack> stacks) {
        int count = 0;
        for (ItemStack stack : stacks.values()) {
            count += stack.getAmount();
        }

        return count;
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.gems.deposit", "skyfactions.gems");
    }
}
