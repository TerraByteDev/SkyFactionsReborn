package net.skullian.skyfactions.command.runes.subcommands;

import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
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
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        RunesAPI.getRunes(player.getUniqueId()).whenComplete((runes, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                ErrorHandler.handleError(player, "get your runes balance", "SQL_RUNES_MODIFY", ex);

                return;
            }

            Messages.RUNES_BALANCE_MESSAGE.send(player, "%count%", runes);
        });
    }

    public static List<String> permissions = List.of("skyfactions.runes.balance", "skyfactions.runes");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
