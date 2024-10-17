package net.skullian.skyfactions.command.runes.subcommands;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.runes.RunesCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
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
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        if (RunesCommandHandler.getSubCommands().size() == 0) {
            Messages.NO_COMMANDS_FOUND.send(player);
        }
        for (int i = 0; i < RunesCommandHandler.getSubCommands().size(); i++) {
            if (!CommandsUtility.hasPerm(player, RunesCommandHandler.getSubCommands().get(i).permission(), false))
                continue;
            Messages.COMMAND_INFO.send(player, "%command_syntax%", RunesCommandHandler.getSubCommands().get(i).getSyntax(), "%command_name%", RunesCommandHandler.getSubCommands().get(i).getName(), "%command_description%", RunesCommandHandler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(player);
    }

    public static List<String> permissions = List.of("skyfactions.runes.help", "skyfactions.runes");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
