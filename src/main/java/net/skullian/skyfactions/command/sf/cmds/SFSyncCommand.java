package net.skullian.skyfactions.command.sf.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

import java.util.List;

@Command("sf")
public class SFSyncCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "sync";
    }

    @Override
    public String getDescription() {
        return "Force a database periodic sync. Do not spam.";
    }

    @Override
    public String getSyntax() {
        return "/sf sync";
    }

    @Command("sync")
    public void perform(
            CommandSourceStack commandSourceStack
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if ((sender instanceof Player) &&!CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        if ((sender instanceof Player) && CommandsUtility.manageCooldown((Player) sender)) return;

        SLogger.warn("[{}] is forcing a database sync.", sender.getName());

        SkyFactionsReborn.cacheService.cacheOnce().whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(sender, "force a database sync", "SQL_CACHE_FAILURE", ex);
                return;
            }

            Messages.SYNC_SUCCESS.send(sender);
        });
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.sync");
    }
}
