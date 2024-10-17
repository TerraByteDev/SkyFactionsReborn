package net.skullian.skyfactions.command.sf.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.entity.Player;

import java.util.List;


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

    @Override
    public void perform(Player player, String[] args) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        SLogger.warn("[{}] is reloading SkyFactionsReborn.", player.getName());
        Messages.RELOADING.send(player);

        SkyFactionsReborn.configHandler.reloadFiles();
        Messages.RELOADED.send(player);
        SLogger.warn("SkyFactionsReborn reloaded.");
    }

    public static List<String> permissions = List.of("skyfactions.reload");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
