package net.skullian.skyfactions.common.command.runes;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("runes")
public class RunesBalanceCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "runes";
    }

    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String getDescription() {
        return "Get your runes balance.";
    }

    @Override
    public String getSyntax() {
        return "/runes balance";
    }

    @Command("balance")
    @Permission(value = {"skyfactions.runes.balance", "skyfactions.runes"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (island == null) {
                Messages.NO_ISLAND.send(player, locale);
                return;
            }

            player.getRunes().whenComplete((runes, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "get your runes balance", "SQL_RUNES_GET", throwable);
                    return;
                }
                Messages.RUNES_BALANCE_MESSAGE.send(player, locale, "count", runes);
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.runes.balance", "skyfactions.runes");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
