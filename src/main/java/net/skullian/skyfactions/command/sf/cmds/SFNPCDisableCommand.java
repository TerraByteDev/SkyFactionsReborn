package net.skullian.skyfactions.command.sf.cmds;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.SLogger;

@Command("sf")
public class SFNPCDisableCommand extends CommandTemplate {

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
    public void perform(
        CommandSender sender
    ) {
        
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        String locale = sender instanceof Player ? ((Player) sender).locale().getLanguage() : Messages.getDefaulLocale();

        SLogger.warn("[{}] is disabling SkyFactions NPCs.", sender.getName());
        Messages.NPC_DISABLING.send(sender, locale);

        SkyFactionsReborn.getNpcManager().updateNPCs(false).whenComplete((affected, exc) -> {
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
