package net.skullian.skyfactions.paper.command.discord.cmds;

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

public class UnlinkCommand extends CommandTemplate {
    @Command("unlink")
    @Permission(value = {"skyfactions.command.unlink"}, mode = Permission.Mode.ANY_OF)
    public void handleUnlink(
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, List.of("skyfactions.command.unlink", "skyfactions.discord"), true)) return;

        SpigotPlayerAPI.getPlayerData(player.getUniqueId()).whenComplete((data, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "unlink your Discord", "SQL_DISCORD_UNLINK", ex);
                return;
            }

            if (data.getDISCORD_ID() == null) {
                Messages.DISCORD_NOT_LINKED.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
            } else {
                SkyFactionsReborn.getCacheService().getEntry(player.getUniqueId()).setNewDiscordID(player.getUniqueId(), "none");
                Messages.DISCORD_UNLINK_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
            }
        });

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
