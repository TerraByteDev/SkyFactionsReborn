package net.skullian.skyfactions.common.command.sf.cmds;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SLogger;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("sf")
public class SFSyncCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "sf";
    }

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
            SkyUser sender
    ) {
        if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, permission(), true)) return;
        String locale = sender.isConsole() ? SkyApi.getInstance().getPlayerAPI().getLocale(sender.getUniqueId()) : Messages.getDefaulLocale();

        SLogger.warn("[{}] is forcing a database sync.", sender.getName());

        Messages.SYNC_RUNNING.send(sender, locale);
        SkyApi.getInstance().getCacheService().cacheOnce().whenComplete((ignored, ex) -> {
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
