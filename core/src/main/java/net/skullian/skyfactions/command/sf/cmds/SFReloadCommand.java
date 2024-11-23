package net.skullian.skyfactions.command.sf.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("sf")
public class SFReloadCommand extends CommandTemplate {
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
            CommandSender sender
    ) {
        
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        String locale = sender instanceof Player ? ((Player) sender).locale().getLanguage() : Messages.getDefaulLocale();

        SLogger.warn("[{}] is reloading SkyFactionsReborn.", sender.getName());
        Messages.RELOADING.send(sender, locale);

        SkyFactionsReborn.getConfigHandler().reloadFiles();

        Messages.RELOADED.send(sender, locale);
        SLogger.warn("SkyFactionsReborn reloaded.");
    }

    public static List<String> permissions = List.of("skyfactions.sf.reload");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
