package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;
        if (args.length < 2) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        } else {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
            if (!offlinePlayer.hasPlayedBefore()) {
                Messages.UNKNOWN_PLAYER.send(player, "%player%", args[1]);
            } else {

                int toPay = Integer.parseInt(args[2]);
                GemsAPI.getGems(player).whenComplete((playerGemCount, ex) -> {
                    if (ex != null) {
                        ErrorHandler.handleError(player, "pay gems to another player", "SQL_GEMS_GET", ex);
                        return;
                    }

                    if (playerGemCount >= toPay) {
                        GemsAPI.subtractGems(player.getUniqueId(), toPay);
                        GemsAPI.addGems(offlinePlayer.getUniqueId(), toPay);

                        Messages.GEM_ADD_SUCCESS.send(player, "%amount%", toPay, "%player%", offlinePlayer.getName());
                    } else {
                        Messages.INSUFFICIENT_GEMS_COUNT.send(player);
                    }
                });


            }

        }
    }

    public static List<String> permissions = List.of("skyfactions.gems.pay", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
