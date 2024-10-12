package net.skullian.skyfactions.command.runes;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.command.runes.subcommands.RunesBalanceCommand;
import net.skullian.skyfactions.command.runes.subcommands.RunesGiveCommand;
import net.skullian.skyfactions.command.runes.subcommands.RunesHelpCommand;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RunesCommandHandler implements CommandExecutor {
    private static ArrayList<CommandTemplate> subcommands = new ArrayList<>();

    public RunesCommandHandler() {
        subcommands.add(new RunesBalanceCommand());
        subcommands.add(new RunesGiveCommand());
        subcommands.add(new RunesHelpCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            if (strings.length > 0) {
                for (int i = 0; i < getSubCommands().size(); i++) {
                    if (strings[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                        getSubCommands().get(i).perform(player, strings);
                    }
                }
            } else if (strings.length == 0) {
                if (CooldownHandler.manageCooldown(player)) return true;
                if (!player.hasPermission("skyfactions.runes.help")) {
                    Messages.PERMISSION_DENY.send(player);
                    return true;
                }

                if (getSubCommands().size() <= 0) {
                    Messages.NO_COMMANDS_FOUND.send(player);
                    return true;
                }
                for (int i = 0; i < getSubCommands().size(); i++) {
                    if (!PermissionsHandler.hasPerm(player, getSubCommands().get(i).permission(), false)) continue;
                    Messages.COMMAND_INFO.send(player, "%command_syntax%", getSubCommands().get(i).getSyntax(), "%command_name%", getSubCommands().get(i).getName(), "%command_description%", getSubCommands().get(i).getDescription());
                }
            }
        }

        return true;
    }

    public static ArrayList<CommandTemplate> getSubCommands() {
        return subcommands;
    }
}