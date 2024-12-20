package net.skullian.skyfactions.common.command.sf;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.common.module.SkyModuleManager;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("sf")
public class SFReloadCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "sf";
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin, refreshes configs, etc.";
    }

    @Override
    public String getSyntax() {
        return "/sf reload";
    }

    @Command("reload")
    @Permission(value = {"skyfactions.sf.reload"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser sender
    ) {
        if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, permission(), true)) return;
        String locale = sender.isConsole() ? SkyApi.getInstance().getPlayerAPI().getLocale(sender.getUniqueId()) : Messages.getDefaulLocale();

        SLogger.warn("[{}] is reloading SkyFactionsReborn.", sender.getName());
        Messages.RELOADING.send(sender, locale);

        SkyApi.getInstance().getConfigHandler().reloadFiles();
        SkyModuleManager.onReload();

        Messages.RELOADED.send(sender, locale);
        SLogger.warn("SkyFactionsReborn reloaded.");
    }

    public static List<String> permissions = List.of("skyfactions.sf.reload");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
