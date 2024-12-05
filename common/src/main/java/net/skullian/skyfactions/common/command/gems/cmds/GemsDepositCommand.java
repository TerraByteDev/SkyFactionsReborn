package net.skullian.skyfactions.common.command.gems.cmds;

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
public class GemsDepositCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "gems";
    }

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
            SkyUser player,
            @Argument(value = "amount") String amount
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", throwable);
                return;
            } else if (island == null) {
                Messages.NO_ISLAND.send(player, locale);
                return;
            }

            SkyItemStack currencyItem = SkyApi.getInstance().getGemsAPI().createGemsStack(player);
            int totalDeposited = 0;

            if (amount.equalsIgnoreCase("all")) {
                totalDeposited = SkyApi.getInstance().getGemsAPI().depositAllItems(player, currencyItem);
            } else {
                try {
                    int parsedAmount = Integer.parseInt(amount);
                    totalDeposited = SkyApi.getInstance().getGemsAPI().depositSpecificAmount(player, currencyItem, parsedAmount);
                } catch (NumberFormatException exception) {
                    Messages.INCORRECT_USAGE.send(player, locale, "usage", getSyntax());
                }
            }

            int finalTotalDeposited = totalDeposited;
            player.removeGems(totalDeposited);
            Messages.GEMS_DEPOSIT_SUCCESS.send(player, locale, "amount", finalTotalDeposited);
        });

    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.gems.deposit", "skyfactions.gems");
    }
}
