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
public class SFNPCDisableCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "sf";
    }

    @Override
    public String getName() {
        return "disablenpcs";
    }

    @Override
    public String getDescription() {
        return "Disable all per-island NPCs.";
    }

    @Override
    public String getSyntax() {
        return "/sf disablenpcs";
    }

    @Command("disablenpcs")
    @Permission(value = {"skyfactions.sf.disablenpcs"}, mode = Permission.Mode.ANY_OF)
    public void perform(
        SkyUser sender
    ) {
        if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, permission(), true)) return;
        String locale = sender.isConsole() ? SkyApi.getInstance().getPlayerAPI().getLocale(sender.getUniqueId()) : Messages.getDefaulLocale();

        SLogger.warn("[{}] is disabling SkyFactions NPCs.", sender.getName());
        Messages.NPC_DISABLING.send(sender, locale);

        SkyApi.getInstance().getNPCManager().updateNPCs(false).whenComplete((affected, exc) -> {
            if (exc != null) {
                ErrorUtil.handleError(sender, "disable NPCs", "NPC_DISABLE_EXCEPTION", exc);
                return;
            }

            Messages.NPC_DISABLED.send(sender, locale, "count", affected);
            SLogger.info("Finished disabling all NPCs - [{}] NPCs removed.", affected);
        });   
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.sf.disablenpcs");
    }

}
