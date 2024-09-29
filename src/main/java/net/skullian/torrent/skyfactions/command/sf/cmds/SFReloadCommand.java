package net.skullian.torrent.skyfactions.command.sf.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.util.SLogger;
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
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

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
