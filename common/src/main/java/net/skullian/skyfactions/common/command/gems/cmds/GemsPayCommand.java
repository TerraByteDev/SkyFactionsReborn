package net.skullian.skyfactions.common.command.gems.cmds;

import net.skullian.skyfactions.paper.api.SpigotGemsAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("gems")
public class GemsPayCommand extends CommandTemplate {

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
            Player player,
            @Argument(value = "target") String playerName,
            @Argument(value = "amount") int amount
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        SpigotPlayerAPI.isPlayerRegistered(offlinePlayer.getUniqueId()).whenComplete((isRegistered, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "check if that player is registered", "SQL_PLAYER_GET", ex);
                return;
            } else if (!isRegistered) {
                Messages.UNKNOWN_PLAYER.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "player", playerName);
                return;
            }

            SpigotGemsAPI.getGems(player.getUniqueId()).whenComplete((playerGemCount, err) -> {
                if (err != null) {
                    ErrorUtil.handleError(player, "get your gems count", "SQL_GEMS_GET", err);
                } else if (playerGemCount >= amount) {
                    SpigotGemsAPI.subtractGems(player.getUniqueId(), amount);
                    SpigotGemsAPI.addGems(offlinePlayer.getUniqueId(), amount);

                    Messages.GEM_ADD_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "amount", amount, "player", offlinePlayer.getName());
                } else {
                    Messages.INSUFFICIENT_GEMS_COUNT.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
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
