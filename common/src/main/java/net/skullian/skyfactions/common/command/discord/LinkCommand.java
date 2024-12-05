package net.skullian.skyfactions.common.command.discord;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.module.SkyModules;
import net.skullian.skyfactions.module.impl.discord.DiscordModule;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class LinkCommand extends CommandTemplate {
    @Command("link")
    @Permission(value = {"skyfactions.command.link"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, List.of("skyfactions.command.link", "skyfactions.discord"), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        DiscordModule module = (DiscordModule) SkyModules.DISCORD.getModule();

        SkyApi.getInstance().getPlayerAPI().getPlayerData(player.getUniqueId()).whenComplete((data, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "link your Discord", "SQL_GET_DISCORD", ex);
                return;
            } else if (data.getDISCORD_ID() == null) {
                module.createLinkCode(player).whenComplete((code, err) -> {
                    if (err != null) {
                        err.printStackTrace();
                        return;
                    }

                    Messages.DISCORD_LINK_PROMPT.send(player, locale, "code", code);
                });
            } else {
                String retrievedUser = module.getUsernameByID(data.getDISCORD_ID());
                Messages.DISCORD_ALREADY_LINKED.send(player, locale, "discord_name", retrievedUser);
            }
        });
    }

    @Override
    public String getParent() {
        return "discord";
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
        return "/discord link";
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.command.link");
    }
}
