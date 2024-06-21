package net.skullian.torrent.skyfactions.command.discord;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnlinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player player) {
            if (!PermissionsHandler.hasPerm(player, "skyfactions.command.unlink", true)) return true;
            if (CooldownHandler.manageCooldown(player)) return true;

            SkyFactionsReborn.db.getDiscordLink(player).thenAccept(linkedID -> {
                if (linkedID == null) {
                    Messages.DISCORD_NOT_LINKED.send(player);
                } else {
                    SkyFactionsReborn.db.registerDiscordLink(player.getUniqueId(), "none").thenAccept(result -> {
                        Messages.DISCORD_UNLINK_SUCCESS.send(player);
                    }).exceptionally(error -> {
                        error.printStackTrace();
                        Messages.ERROR.send(player, "%operation%", "unlink your Discord.", "%debug%", "SQL_DISCORD_UNLINK");
                        return null;
                    });
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "unlink your Discord.", "%debug%", "SQL_GET_DISCORD");
                return null;
            });
        }
        return true;
    }
}
