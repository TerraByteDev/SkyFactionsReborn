package net.skullian.torrent.skyfactions.command.sf.cmds;

import lombok.extern.log4j.Log4j2;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.entity.Player;

import java.util.List;

@Log4j2(topic = "SkyFactionsReborn")
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

        LOGGER.warn("[{}] is reloading SkyFactionsReborn.", player.getName());
        Messages.RELOADING.send(player);

        SkyFactionsReborn.configHandler.reloadFiles();
        Messages.RELOADED.send(player);
        LOGGER.warn("SkyFactionsReborn reloaded.");
    }

    public static List<String> permissions = List.of("skyfactions.reload");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
