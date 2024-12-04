package net.skullian.skyfactions.paper.command.gems.cmds;

import net.skullian.skyfactions.paper.api.SpigotGemsAPI;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
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

        SpigotIslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_GEMS_GET", ex);
                return;
            }

            if (hasIsland) {
                SpigotGemsAPI.getGems(player.getUniqueId()).whenComplete((gems, throwable) -> {
                    if (throwable != null) {
                        ErrorUtil.handleError(player, "get your gems", "SQL_GEMS_GET", throwable);
                        return;
                    }

                    Messages.GEMS_COUNT_MESSAGE.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "count", gems);
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
