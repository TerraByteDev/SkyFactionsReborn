package net.skullian.skyfactions.command.sf.cmds;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.sf.SFCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class SFHelpCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Get help on all general SF commands.";
    }

    @Override
    public String getSyntax() {
        return "/sf help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        if (SFCommandHandler.getSubCommands().size() <= 0) {
            Messages.NO_COMMANDS_FOUND.send(player);
        }
        for (int i = 0; i < SFCommandHandler.getSubCommands().size(); i++) {
            if (!CommandsUtility.hasPerm(player, SFCommandHandler.getSubCommands().get(i).permission(), false))
                continue;
            Messages.COMMAND_INFO.send(player, "%command_syntax%", SFCommandHandler.getSubCommands().get(i).getSyntax(), "%command_name%", SFCommandHandler.getSubCommands().get(i).getName(), "%command_description%", SFCommandHandler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(player);
    }

    public static List<String> permissions = List.of("skyfactions.sf.help");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
