package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.gems.GemsCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("gems")
public class GemsHelpCommand extends CommandTemplate {

    GemsCommandHandler handler;

    public GemsHelpCommand(GemsCommandHandler handler) {
        this.handler = handler;
    }

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

    @Command("help")
    @Permission(value = { "skyfactions.gems.help", "skyfactions.gems" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack
    ) {
        CommandSender sender = commandSourceStack.getSender();
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

    public static List<String> permissions = List.of("skyfactions.gems.help", "skyfactions.gems");

    @Override
    public List<String> permission() {
        return permissions;
    }

}
