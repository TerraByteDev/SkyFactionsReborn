package net.skullian.torrent.skyfactions.command.runes.subcommands;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.command.runes.RunesCommandHandler;
import net.skullian.torrent.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class RunesHelpCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Help on all runes related commands.";
    }

    @Override
    public String getSyntax() {
        return "/runes help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        if (RunesCommandHandler.getSubCommands().size() == 0) {
            Messages.NO_COMMANDS_FOUND.send(player);
        }
        for (int i = 0; i < RunesCommandHandler.getSubCommands().size(); i++) {
            if (!PermissionsHandler.hasPerm(player, RunesCommandHandler.getSubCommands().get(i).permission(), false))
                continue;
            Messages.COMMAND_INFO.send(player, "%command_syntax%", RunesCommandHandler.getSubCommands().get(i).getSyntax(), "%command_name%", GemsCommandHandler.getSubCommands().get(i).getName(), "%command_description%", GemsCommandHandler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(player);
    }

    public static List<String> permissions = List.of("skyfactions.runes.help", "skyfactions.runes");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
