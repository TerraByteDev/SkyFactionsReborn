package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.command.raid.RaidCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

import java.util.List;

@Command("faction")
public class FactionHelpCommand extends CommandTemplate {

    FactionCommandHandler handler;

    public FactionHelpCommand(FactionCommandHandler handler) {
        this.handler = handler;
    }

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

    @Command("help")
    public void perform(
            CommandSender sender
    ) {
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        if ((sender instanceof Player) && CommandsUtility.manageCooldown((Player) sender)) return;

        Messages.COMMAND_HEAD.send(sender);
        if (handler.getSubCommands().isEmpty()) {
            Messages.NO_COMMANDS_FOUND.send(sender);
        }
        for (int i = 0; i < handler.getSubCommands().size(); i++) {
            if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, handler.getSubCommands().get(i).permission(), false))
                continue;
            Messages.COMMAND_INFO.send(sender, "%command_syntax%", handler.getSubCommands().get(i).getSyntax(), "%command_name%", handler.getSubCommands().get(i).getName(), "%command_description%", handler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(sender);
    }

    public static List<String> permissions = List.of("skyfactions.faction.help", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
