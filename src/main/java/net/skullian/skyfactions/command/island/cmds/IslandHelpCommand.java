package net.skullian.skyfactions.command.island.cmds;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.command.island.IslandCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class IslandHelpCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Help on all island related commands.";
    }

    @Override
    public String getSyntax() {
        return "/island help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        if (IslandCommandHandler.getSubCommands().size() <= 0) {
            Messages.NO_COMMANDS_FOUND.send(player);
        }
        for (int i = 0; i < IslandCommandHandler.getSubCommands().size(); i++) {
            if (!PermissionsHandler.hasPerm(player, IslandCommandHandler.getSubCommands().get(i).permission(), false))
                continue;
            Messages.COMMAND_INFO.send(player, "%command_syntax%", IslandCommandHandler.getSubCommands().get(i).getSyntax(), "%command_name%", IslandCommandHandler.getSubCommands().get(i).getName(), "%command_description%", IslandCommandHandler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(player);
    }

    public static List<String> permissions = List.of("skyfactions.island.help", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
