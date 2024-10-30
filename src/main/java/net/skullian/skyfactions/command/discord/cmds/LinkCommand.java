package net.skullian.skyfactions.command.discord.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.dv8tion.jda.api.entities.User;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class LinkCommand extends CommandTemplate {
    @Command("link")
    @Permission(value = { "skyfactions.command.link" }, mode = Permission.Mode.ANY_OF)
    public boolean handleLink(
            CommandSourceStack commandSourceStack
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (sender instanceof Player player) {
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
                    Messages.DISCORD_LINK_PROMPT.send(player, player.locale(), "%code%", generatedCode);
                } else {
                    User retrivedUser = SkyFactionsReborn.discordHandler.JDA.getUserById(id);
                    Messages.DISCORD_ALREADY_LINKED.send(player, player.locale(), "%discord_name%", retrivedUser.getName());
                }
            });
        }

        return true;
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getDescription() {
        return "Link your discord account for raid notifications, etc.";
    }

    @Override
    public String getSyntax() {
        return "/link";
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.command.link");
    }
}
