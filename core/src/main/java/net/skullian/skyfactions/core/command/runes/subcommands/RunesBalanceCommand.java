package net.skullian.skyfactions.core.command.runes.subcommands;

import net.skullian.skyfactions.core.api.SpigotIslandAPI;
import net.skullian.skyfactions.core.api.SpigotRunesAPI;
import net.skullian.skyfactions.core.command.CommandTemplate;
import net.skullian.skyfactions.core.command.CommandsUtility;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.core.util.ErrorUtil;
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

        SpigotIslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (!hasIsland) {
                Messages.NO_ISLAND.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return;
            }
            SpigotRunesAPI.getRunes(player.getUniqueId()).whenComplete((runes, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "get your runes balance", "SQL_RUNES_GET", throwable);
                    return;
                }
                Messages.RUNES_BALANCE_MESSAGE.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "count", runes);
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.runes.balance", "skyfactions.runes");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
