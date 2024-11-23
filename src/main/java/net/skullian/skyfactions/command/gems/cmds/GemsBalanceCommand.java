package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("gems")
public class GemsBalanceCommand extends CommandTemplate {

    @Override
    public String getName() {
        return "balance";
    }

    @Override
    public String getDescription() {
        return "Get your gems balance.";
    }

    @Override
    public String getSyntax() {
        return "/gems balance";
    }

    @Command("balance")
    @Permission(value = {"skyfactions.gems.balance", "skyfactions.gems"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        IslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_GEMS_GET", ex);
                return;
            }

            if (hasIsland) {
                GemsAPI.getGems(player.getUniqueId()).whenComplete((gems, throwable) -> {
                    if (throwable != null) {
                        ErrorUtil.handleError(player, "get your gems", "SQL_GEMS_GET", throwable);
                        return;
                    }

                    Messages.GEMS_COUNT_MESSAGE.send(player, PlayerAPI.getLocale(player.getUniqueId()), "count", gems);
                });
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.gems.balance", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
