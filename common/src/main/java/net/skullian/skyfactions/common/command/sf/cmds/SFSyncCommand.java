package net.skullian.skyfactions.common.command.sf.cmds;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import net.skullian.skyfactions.paper.util.SLogger;
import org.incendo.cloud.annotations.Permission;

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
    @Permission(value = {"skyfactions.sf.sync"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSender sender
    ) {
        
        if ((sender instanceof Player) &&!CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        String locale = sender instanceof Player ? ((Player) sender).locale().getLanguage() : Messages.getDefaulLocale();

        SLogger.warn("[{}] is forcing a database sync.", sender.getName());

        Messages.SYNC_RUNNING.send(sender, locale);
        SkyFactionsReborn.getCacheService().cacheOnce().whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(sender, "force a database sync", "SQL_CACHE_FAILURE", ex);
                return;
            }

            Messages.SYNC_SUCCESS.send(sender, locale);
        });
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.sf.sync");
    }
}
