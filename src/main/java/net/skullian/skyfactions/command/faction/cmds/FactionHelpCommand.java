package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.command.raid.RaidCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionHelpCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Help on all Faction related commands.";
    }

    @Override
    public String getSyntax() {
        return "/faction help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        if (RaidCommandHandler.getSubCommands().size() <= 0) {
            Messages.NO_COMMANDS_FOUND.send(player);
        }
        for (int i = 0; i < RaidCommandHandler.getSubCommands().size(); i++) {
            if (!CommandsUtility.hasPerm(player, RaidCommandHandler.getSubCommands().get(i).permission(), false))
                continue;
            Messages.COMMAND_INFO.send(player, "%command_syntax%", FactionCommandHandler.getSubCommands().get(i).getSyntax(), "%command_name%", RaidCommandHandler.getSubCommands().get(i).getName(), "%command_description%", RaidCommandHandler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(player);
    }

    public static List<String> permissions = List.of("skyfactions.faction.help", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
