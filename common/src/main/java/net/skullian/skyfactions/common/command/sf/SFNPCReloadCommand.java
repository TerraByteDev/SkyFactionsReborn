package net.skullian.skyfactions.common.command.sf;

import java.util.List;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SLogger;
import org.incendo.cloud.annotations.Command;

import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import org.incendo.cloud.annotations.Permission;

@Command("sf")
public class SFNPCReloadCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "sf";
    }

    @Override
    public String getName() {
        return "updatenpcs";
    }

    @Override
    public String getDescription() {
        return "Reload all NPC attributes.";
    }

    @Override
    public String getSyntax() {
        return "/sf updatenpcs";
    }

    @Command("updatenpcs")
    @Permission(value = {"skyfactions.sf.updatenpcs"}, mode = Permission.Mode.ANY_OF)
    public void perform(
        SkyUser sender
    ) {
        if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, permission(), true)) return;
        String locale = sender.isConsole() ? SkyApi.getInstance().getPlayerAPI().getLocale(sender.getUniqueId()) : Messages.getDefaulLocale();

        SLogger.warn("[{}] is reloading SkyFactions NPCs.", sender.getName());
        Messages.NPC_RELOADING.send(sender, locale);

        SkyApi.getInstance().getNPCManager().updateNPCs(false).whenComplete((affected, exc) -> {
            if (exc != null) {
                ErrorUtil.handleError(sender, "update NPCs", "NPC_RELOAD_EXCEPTION", exc);
                return;
            }

            Messages.NPC_RELOADED.send(sender, locale, "count", affected);
            SLogger.info("Finished reloading NPCs - [{}] NPCs affected.", affected);
        });   
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.sf.updatenpcs", "skyfactions.sf");
    }

}
