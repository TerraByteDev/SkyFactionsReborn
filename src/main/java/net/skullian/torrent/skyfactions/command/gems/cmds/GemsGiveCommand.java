package net.skullian.torrent.skyfactions.command.gems.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class GemsGiveCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives other players gems (ADMIN COMMAND)";
    }

    @Override
    public String getSyntax() {
        return "/gems give <player> <amount>";
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
                SkyFactionsReborn.db.getGems(offlinePlayer.getPlayer()).thenAccept(gems -> {
                    SkyFactionsReborn.db.addGems(offlinePlayer.getPlayer(), gems, Integer.parseInt(args[2])).thenAccept(result -> {
                        Messages.GEM_ADD_SUCCESS.send(player, "%amount%", args[2], "%player%", offlinePlayer.getName());
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        Messages.ERROR.send(player, "%operation%", "give gems");
                        return null;
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    Messages.ERROR.send(player, "%operation%", "give gems");
                    return null;
                });
            }

        }
    }

    @Override
    public String permission() {
        return "skyfactions.gems.give";
    }
}