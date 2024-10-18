package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

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

    @Command("pay <player> <amount>")
    public void perform(
            CommandSender sender,
            @Argument(value = "player") String playerName,
            @Argument(value = "amount") int amount
    ) {
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (!offlinePlayer.hasPlayedBefore()) {
            Messages.UNKNOWN_PLAYER.send(sender, "%player%", playerName);
        } else {

            GemsAPI.getGems(player.getUniqueId()).whenComplete((playerGemCount, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "pay gems to another player", "SQL_GEMS_GET", ex);
                    return;
                }

                if (playerGemCount >= amount) {
                    GemsAPI.subtractGems(player.getUniqueId(), amount);
                    GemsAPI.addGems(offlinePlayer.getUniqueId(), amount);

                    Messages.GEM_ADD_SUCCESS.send(player, "%amount%", amount, "%player%", offlinePlayer.getName());
                } else {
                    Messages.INSUFFICIENT_GEMS_COUNT.send(player);
                }
            });
        }
    }

    public static List<String> permissions = List.of("skyfactions.gems.pay", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
