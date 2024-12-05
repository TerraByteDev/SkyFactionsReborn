package net.skullian.skyfactions.common.command.gems.cmds;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("gems")
public class GemsBalanceCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "gems";
    }

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
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_GEMS_GET", ex);
            } else if (island != null) {
                player.getGems().whenComplete((gems, throwable) -> {
                    if (throwable != null) {
                        ErrorUtil.handleError(player, "get your gems", "SQL_GEMS_GET", throwable);
                        return;
                    }

                    Messages.GEMS_COUNT_MESSAGE.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "count", gems);
                });
            } else Messages.NO_ISLAND.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
        });
    }

    public static List<String> permissions = List.of("skyfactions.gems.balance", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
