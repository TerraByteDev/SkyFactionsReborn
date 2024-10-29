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
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SLogger;

@Command("sf")
public class SFNPCReloadCommand extends CommandTemplate {

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
    public void perform(
        CommandSourceStack commandSourceStack
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if ((sender instanceof Player) &&!CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        if ((sender instanceof Player) && CommandsUtility.manageCooldown((Player) sender)) return;

        SLogger.warn("[{}] is reloading SkyFactions NPCs.", sender.getName());
        Messages.NPC_RELOADING.send(sender);

        SkyFactionsReborn.npcManager.reloadNPCs().whenComplete((affected, exc) -> {
            if (exc != null) {
                ErrorHandler.handleError(sender, "update NPCs", "NPC_RELOAD_EXCEPTION", exc);
                return;
            }

            Messages.NPC_RELOADED.send(sender, "%count%", affected);
        });   
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.sf.updatenpcs", "skyfactions.sf");
    }

}