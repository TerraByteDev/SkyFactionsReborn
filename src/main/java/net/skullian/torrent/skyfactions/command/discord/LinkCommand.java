package net.skullian.torrent.skyfactions.command.discord;

import net.dv8tion.jda.api.entities.User;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player player) {
            if (!PermissionsHandler.hasPerm(player, "skyfactions.command.link", true)) return true;
            if (CooldownHandler.manageCooldown(player)) return true;

            SkyFactionsReborn.db.getDiscordLink(player).thenAccept(linkedID -> {
                if (linkedID == null) {
                    String generatedCode = SkyFactionsReborn.dc.createLinkCode(player);

                    Messages.DISCORD_LINK_PROMPT.send(player, "%code%", generatedCode);
                } else {
                    User retrievedUser = SkyFactionsReborn.dc.JDA.getUserById(linkedID);
                    Messages.DISCORD_ALREADY_LINKED.send(player, "%discord_name%", retrievedUser.getName());
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(commandSender, "%operation%", "link your Discord.");
                return null;
            });
        }

        return true;
    }
}
