package net.skullian.skyfactions.common.command.discord.cmds;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("discord")
public class UnlinkCommand extends CommandTemplate {
    @Command("unlink")
    @Permission(value = {"skyfactions.command.unlink"}, mode = Permission.Mode.ANY_OF)
    public void handleUnlink(
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, List.of("skyfactions.command.unlink", "skyfactions.discord"), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getPlayerAPI().getPlayerData(player.getUniqueId()).whenComplete((data, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "unlink your Discord", "SQL_DISCORD_UNLINK", ex);
                return;
            }

            if (data.getDISCORD_ID() == null) {
                Messages.DISCORD_NOT_LINKED.send(player, locale);
            } else {
                SkyApi.getInstance().getCacheService().getEntry(player.getUniqueId()).setNewDiscordID(player.getUniqueId(), "none");
                Messages.DISCORD_UNLINK_SUCCESS.send(player, locale);
            }
        });

    }

    @Override
    public String getParent() {
        return "discord";
    }

    @Override
    public String getName() {
        return "unlink";
    }

    @Override
    public String getDescription() {
        return "Remove the link between your discord account and your SkyFactions account.";
    }

    @Override
    public String getSyntax() {
        return "/discord unlink";
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.command.unlink");
    }
}
