package net.skullian.skyfactions.command.discord.cmds;

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

public class UnlinkCommand extends CommandTemplate {
    @Command("unlink")
    @Permission(value = {"skyfactions.command.unlink"}, mode = Permission.Mode.ANY_OF)
    public boolean handleUnlink(
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, List.of("skyfactions.command.unlink", "skyfactions.discord"), true))
            return true;

        SkyFactionsReborn.getDatabaseManager().getPlayerManager().getDiscordID(player).whenComplete((id, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "unlink your Discord", "SQL_DISCORD_UNLINK", ex);
                return;
            }

            if (id == null) {
                Messages.DISCORD_NOT_LINKED.send(player, PlayerAPI.getLocale(player.getUniqueId()));
            } else {
                Messages.DISCORD_UNLINK_SUCCESS.send(player, PlayerAPI.getLocale(player.getUniqueId()));
            }
        });
        return true;
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
        return "/unlink";
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.command.unlink");
    }
}
