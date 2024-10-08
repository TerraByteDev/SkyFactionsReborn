package net.skullian.torrent.skyfactions.command.gems.cmds;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.command.gems.GemsCommandHandler;
import net.skullian.torrent.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class GemsHelpCommand extends CommandTemplate {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Help on all gem related commands.";
    }

    @Override
    public String getSyntax() {
        return "/gems help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        if (GemsCommandHandler.getSubCommands().size() == 0) {
            Messages.NO_COMMANDS_FOUND.send(player);
        }
        for (int i = 0; i < GemsCommandHandler.getSubCommands().size(); i++) {
            if (!PermissionsHandler.hasPerm(player, GemsCommandHandler.getSubCommands().get(i).permission(), false))
                continue;
            Messages.COMMAND_INFO.send(player, "%command_syntax%", GemsCommandHandler.getSubCommands().get(i).getSyntax(), "%command_name%", GemsCommandHandler.getSubCommands().get(i).getName(), "%command_description%", GemsCommandHandler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(player);
    }

    public static List<String> permissions = List.of("skyfactions.gems.help", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }

}
