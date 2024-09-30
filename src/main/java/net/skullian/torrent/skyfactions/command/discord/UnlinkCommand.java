package net.skullian.torrent.skyfactions.command.discord;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnlinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player player) {
            if (!PermissionsHandler.hasPerm(player, List.of("skyfactions.command.unlink", "skyfactions.discord"), true))
                return true;
            if (CooldownHandler.manageCooldown(player)) return true;

            SkyFactionsReborn.db.getDiscordLink(player).whenCompleteAsync((id, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "unlink your Discord", "SQL_DISCORD_UNLINK", ex);
                    return;
                }

                if (id == null) {
                    Messages.DISCORD_NOT_LINKED.send(player);
                } else {
                    Messages.DISCORD_UNLINK_SUCCESS.send(player);
                }
            });
        }
        return true;
    }
}
