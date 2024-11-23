package net.skullian.skyfactions.command.runes.subcommands;

import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("runes")
public class RunesBalanceCommand extends CommandTemplate {
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
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        IslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (!hasIsland) {
                Messages.NO_ISLAND.send(player, PlayerAPI.getLocale(player.getUniqueId()));
                return;
            }
            RunesAPI.getRunes(player.getUniqueId()).whenComplete((runes, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "get your runes balance", "SQL_RUNES_GET", throwable);
                    return;
                }
                Messages.RUNES_BALANCE_MESSAGE.send(player, PlayerAPI.getLocale(player.getUniqueId()), "count", runes);
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.runes.balance", "skyfactions.runes");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
