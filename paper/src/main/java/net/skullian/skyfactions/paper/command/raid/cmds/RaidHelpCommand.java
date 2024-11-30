package net.skullian.skyfactions.paper.command.raid.cmds;

import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.CommandsUtility;
import net.skullian.skyfactions.paper.command.raid.RaidCommandHandler;
import net.skullian.skyfactions.paper.config.types.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("raid")
public class RaidHelpCommand extends CommandTemplate {

    RaidCommandHandler handler;

    public RaidHelpCommand(RaidCommandHandler handler) {
        this.handler = handler;
    }

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

    @Command("help")
    @Permission(value = {"skyfactions.raid.help", "skyfactions.raid"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSender sender
    ) {
        
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        String locale = sender instanceof Player ? ((Player) sender).locale().getLanguage() : Messages.getDefaulLocale();

        Messages.COMMAND_HEAD.send(sender, locale);
        if (handler.getSubCommands().isEmpty()) {
            Messages.NO_COMMANDS_FOUND.send(sender, locale);
        } else {
            for (CommandTemplate command : handler.getSubCommands().values()) {
                if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, command.permission(), false))
                    continue;
                Messages.COMMAND_INFO.send(sender, locale, "command_syntax", command.getSyntax(), "command_name", command.getName(), "command_description", command.getDescription());
            }
        }
        Messages.COMMAND_HEAD.send(sender, locale);
    }

    public static List<String> permissions = List.of("skyfactions.raid.help", "skyfactions.raid");

    @Override
    public List<String> permission() {
        return permissions;
    }
}