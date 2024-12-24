package net.skullian.skyfactions.common.command.gems;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.List;

@Command("gems")
public class GemsWithdrawCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "gems";
    }

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
            SkyUser player,
            @Argument(value = "amount") String amount
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_GEMS_GET", throwable);
            } else if (island != null) {
                player.getGems().whenComplete((gems, err) -> {
                    if (err != null) {
                        ErrorUtil.handleError(player, "get your gems", "SQL_GEMS_GET", err);
                        return;
                    }

                    try {
                        int parsedAmount = amount.equalsIgnoreCase("all") ? gems : (Integer.parseInt(amount) > gems ? gems : Integer.parseInt(amount));
                        SkyItemStack stack = SkyApi.getInstance().getGemsAPI().createGemsStack(player);
                        stack.setAmount(parsedAmount);

                        int remainingItems = SkyApi.getInstance().getGemsAPI().addItemToInventory(player, stack);

                        player.removeGems((parsedAmount - remainingItems));

                        Messages.GEMS_WITHDRAW_SUCCESS.send(player, locale, "amount", parsedAmount);
                        if (remainingItems > 0) {
                            Messages.GEMS_INSUFFICIENT_INVENTORY_SPACE.send(player, locale);
                        }
                    } catch (NumberFormatException exception) {
                        Messages.INCORRECT_USAGE.send(player, locale, "usage", getSyntax());
                    }
                });
            } else {
                Messages.NO_ISLAND.send(player, locale);
            }
        });

    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.gems.withdraw", "skyfactions.gems");
    }
}
