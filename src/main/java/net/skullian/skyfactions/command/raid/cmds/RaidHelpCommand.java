package net.skullian.skyfactions.command.raid.cmds;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.command.raid.RaidCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class RaidHelpCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Help on all raid related commands/";
    }

    @Override
    public String getSyntax() {
        return "/raid help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        if (RaidCommandHandler.getSubCommands().size() <= 0) {
            Messages.NO_COMMANDS_FOUND.send(player);
        }
        for (int i = 0; i < RaidCommandHandler.getSubCommands().size(); i++) {
            if (!PermissionsHandler.hasPerm(player, RaidCommandHandler.getSubCommands().get(i).permission(), false))
                continue;
            Messages.COMMAND_INFO.send(player, "%command_syntax%", RaidCommandHandler.getSubCommands().get(i).getSyntax(), "%command_name%", RaidCommandHandler.getSubCommands().get(i).getName(), "%command_description%", RaidCommandHandler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(player);
    }

    public static List<String> permissions = List.of("skyfactions.raid.help", "skyfactions.raid");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
