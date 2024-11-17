package net.skullian.skyfactions.command.discord.cmds;

import net.dv8tion.jda.api.entities.User;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.PlayerSource;

import java.util.List;

public class LinkCommand extends CommandTemplate {
    @Command("link")
    @Permission(value = {"skyfactions.command.link"}, mode = Permission.Mode.ANY_OF)
    public boolean handleLink(
            Player player
    ) {

            if (!CommandsUtility.hasPerm(player, List.of("skyfactions.command.link", "skyfactions.discord"), true))
                return true;

            SkyFactionsReborn.getDatabaseManager().getPlayerManager().getDiscordID(player).whenComplete((id, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "link your Discord", "SQL_GET_DISCORD", ex);
                    return;
                }

                if (id == null) {
                    String generatedCode = SkyFactionsReborn.getDiscordHandler().createLinkCode(player);
                    Messages.DISCORD_LINK_PROMPT.send(player, PlayerHandler.getLocale(player.getUniqueId()), "code", generatedCode);
                } else {
                    User retrivedUser = SkyFactionsReborn.getDiscordHandler().JDA.getUserById(id);
                    Messages.DISCORD_ALREADY_LINKED.send(player, PlayerHandler.getLocale(player.getUniqueId()), "discord_name", retrivedUser.getName());
                }
            });

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
