package net.skullian.skyfactions.command.discord.cmds;

import net.dv8tion.jda.api.entities.User;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.util.ErrorUtil;
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

        PlayerAPI.getPlayerData(player.getUniqueId()).whenComplete((data, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "link your Discord", "SQL_GET_DISCORD", ex);
                return;
            } else if (data.getDISCORD_ID() == null) {
                String generatedCode = SkyFactionsReborn.getDiscordHandler().createLinkCode(player);
                Messages.DISCORD_LINK_PROMPT.send(player, PlayerAPI.getLocale(player.getUniqueId()), "code", generatedCode);
            } else {
                User retrivedUser = SkyFactionsReborn.getDiscordHandler().JDA.getUserById(data.getDISCORD_ID());
                Messages.DISCORD_ALREADY_LINKED.send(player, PlayerAPI.getLocale(player.getUniqueId()), "discord_name", retrivedUser.getName());
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
