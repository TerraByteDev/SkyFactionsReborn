package net.skullian.torrent.skyfactions.command.gems.cmds;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.api.GemsAPI;
import net.skullian.torrent.skyfactions.config.Messages;
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
                int playerGemCount = GemsAPI.getGems(player);

                if (playerGemCount >= toPay) {
                    GemsAPI.subtractGems(player, toPay);
                    GemsAPI.addGems(offlinePlayer.getPlayer(), toPay);

                    Messages.GEM_ADD_SUCCESS.send(player, "%amount%", toPay, "%player%", offlinePlayer.getName());
                } else {
                    Messages.INSUFFICIENT_GEMS_COUNT.send(player);
                }


            }

        }
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.gems.pay", "skyfactions.gems", "skyfactions.player");
    }
}
