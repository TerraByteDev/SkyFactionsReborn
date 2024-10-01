package net.skullian.torrent.skyfactions.command.runes.subcommands;

import net.skullian.torrent.skyfactions.api.RunesAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class RunesGiveCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives other players runes (ADMIN COMMAND)";
    }

    @Override
    public String getSyntax() {
        return "/runes give <player> <amount>";
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
                RunesAPI.addRunes(offlinePlayer.getUniqueId(), Integer.parseInt(args[2])).whenCompleteAsync((ignored, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        ErrorHandler.handleError(player, "give runes to another player", "SQL_RUNES_GIVE", throwable);
                        return;
                    }

                    Messages.RUNES_GIVE_SUCCESS.send(player, "%amount%", args[2], "%player%", offlinePlayer.getName());
                });
            }

        }
    }

    public static List<String> permissions = List.of("skyfactions.runes.give");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
