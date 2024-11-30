package net.skullian.skyfactions.paper.command.discord.cmds;

import net.dv8tion.jda.api.entities.User;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class LinkCommand extends CommandTemplate {
    @Command("link")
    @Permission(value = {"skyfactions.command.link"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player
    ) {

        if (!CommandsUtility.hasPerm(player, List.of("skyfactions.command.link", "skyfactions.discord"), true)) return;

        SpigotPlayerAPI.getPlayerData(player.getUniqueId()).whenComplete((data, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "link your Discord", "SQL_GET_DISCORD", ex);
                return;
            } else if (data.getDISCORD_ID() == null) {
                String generatedCode = SkyFactionsReborn.getDiscordHandler().createLinkCode(player);
                Messages.DISCORD_LINK_PROMPT.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "code", generatedCode);
            } else {
                User retrivedUser = SkyFactionsReborn.getDiscordHandler().JDA.getUserById(data.getDISCORD_ID());
                Messages.DISCORD_ALREADY_LINKED.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "discord_name", retrivedUser.getName());
            }
        });
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
