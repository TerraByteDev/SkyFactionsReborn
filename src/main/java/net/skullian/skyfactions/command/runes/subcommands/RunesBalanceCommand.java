package net.skullian.skyfactions.command.runes.subcommands;

import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public void perform(Player player, String[] args) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        IslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            }

            if (!hasIsland) {
                Messages.NO_ISLAND.send(player);
                return;
            }
            RunesAPI.getRunes(player.getUniqueId()).whenComplete((runes, exc) -> {
                if (exc != null) {
                    ErrorHandler.handleError(player, "get your runes balance", "SQL_RUNES_MODIFY", exc);
                    return;
                }

                Messages.RUNES_BALANCE_MESSAGE.send(player, "%count%", runes);
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.runes.balance", "skyfactions.runes");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
