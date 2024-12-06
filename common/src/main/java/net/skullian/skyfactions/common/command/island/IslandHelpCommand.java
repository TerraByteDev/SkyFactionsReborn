package net.skullian.skyfactions.common.command.island;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("island")
public class IslandHelpCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "island";
    }

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

    @Command("help")
    @Permission(value = {"skyfactions.island.help", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser sender
    ) {
        if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, permission(), true)) return;
        String locale = sender.isConsole() ? SkyApi.getInstance().getPlayerAPI().getLocale(sender.getUniqueId()) : Messages.getDefaulLocale();

        Messages.COMMAND_HEAD.send(sender, locale);
        if (SkyApi.getInstance().getCommandHandler().getSubCommands(getParent()).isEmpty()) {
            Messages.NO_COMMANDS_FOUND.send(sender, locale);
        } else {
            for (CommandTemplate command : SkyApi.getInstance().getCommandHandler().getSubCommands(getParent()).values()) {
                if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, command.permission(), false)) continue;

                Messages.COMMAND_INFO.send(sender, locale, "command_syntax", command.getSyntax(), "command_name", command.getName(), "command_description", command.getDescription());
            }
        }
        Messages.COMMAND_HEAD.send(sender, locale);
    }

    public static List<String> permissions = List.of("skyfactions.island.help", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}