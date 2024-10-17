package net.skullian.skyfactions.command.discord;

import net.dv8tion.jda.api.entities.User;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player player) {
            if (!CommandsUtility.hasPerm(player, List.of("skyfactions.command.link", "skyfactions.discord"), true))
                return true;
            if (CommandsUtility.manageCooldown(player)) return true;

            SkyFactionsReborn.databaseHandler.getDiscordLink(player).whenComplete((id, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "link your Discord", "SQL_GET_DISCORD", ex);
                    return;
                }

                if (id == null) {
                    String generatedCode = SkyFactionsReborn.discordHandler.createLinkCode(player);
                    Messages.DISCORD_LINK_PROMPT.send(player, "%code%", generatedCode);
                } else {
                    User retrivedUser = SkyFactionsReborn.discordHandler.JDA.getUserById(id);
                    Messages.DISCORD_ALREADY_LINKED.send(player, "%discord_name%", retrivedUser.getName());
                }
            });
        }

        return true;
    }
}
