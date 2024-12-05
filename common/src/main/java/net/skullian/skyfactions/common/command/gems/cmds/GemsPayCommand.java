package net.skullian.skyfactions.common.command.gems.cmds;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("gems")
public class GemsPayCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "gems";
    }

    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getDescription() {
        return "Give some of your gems to other players.";
    }

    @Override
    public String getSyntax() {
        return "/gems pay <player> <amount>";
    }

    @Command("pay <target> <amount>")
    @Permission(value = {"skyfactions.gems.pay", "skyfactions.gems"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "target") String playerName,
            @Argument(value = "amount") int amount
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyUser offlinePlayer = SkyApi.getInstance().getUserManager().getUser(playerName);
        SkyApi.getInstance().getPlayerAPI().isPlayerRegistered(offlinePlayer.getUniqueId()).whenComplete((isRegistered, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "check if that player is registered", "SQL_PLAYER_GET", ex);
                return;
            } else if (!isRegistered) {
                Messages.UNKNOWN_PLAYER.send(player, locale, "player", playerName);
                return;
            }

            player.getGems().whenComplete((playerGemCount, err) -> {
                if (err != null) {
                    ErrorUtil.handleError(player, "get your gems count", "SQL_GEMS_GET", err);
                } else if (playerGemCount >= amount) {
                    player.removeGems(amount);
                    offlinePlayer.addGems(amount);

                    Messages.GEMS_PAY_SUCCESS.send(player, locale, "amount", amount, "player", offlinePlayer.getName());
                    if (offlinePlayer.isOnline()) Messages.GEMS_PAY_NOTIFY.send(player, "player_name", player.getName(), "amount", amount);
                } else {
                    Messages.INSUFFICIENT_GEMS_COUNT.send(player, locale);
                }
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.gems.pay", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
