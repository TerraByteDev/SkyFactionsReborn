package net.skullian.skyfactions.command.faction;

import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.faction.cmds.*;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FactionCommandHandler implements CommandExecutor {

    private static ArrayList<CommandTemplate> subcommands = new ArrayList<>();

    public FactionCommandHandler() {
        subcommands.add(new FactionCreateCommand());
        subcommands.add(new FactionInfoCommand());
        subcommands.add(new FactionHelpCommand());
        subcommands.add(new FactionTeleportCommand());
        subcommands.add(new FactionMOTDCommand());
        subcommands.add(new FactionLeaveCommand());
        subcommands.add(new FactionBroadcastCommand());
        subcommands.add(new FactionInviteCommand());
        subcommands.add(new FactionRequestJoinCommand());
        subcommands.add(new FactionDonateCommand());
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
                if (CommandsUtility.manageCooldown(player)) return true;
                if (!player.hasPermission("skyfactions.faction.help")) {
                    Messages.PERMISSION_DENY.send(player);
                    return true;
                }

                Messages.COMMAND_HEAD.send(player);
                if (getSubCommands().size() <= 0) {
                    Messages.NO_COMMANDS_FOUND.send(player);
                }
                for (int i = 0; i < getSubCommands().size(); i++) {
                    if (!CommandsUtility.hasPerm(player, getSubCommands().get(i).permission(), false)) continue;
                    Messages.COMMAND_INFO.send(player, "%command_syntax%", getSubCommands().get(i).getSyntax(), "%command_name%", getSubCommands().get(i).getName(), "%command_description%", getSubCommands().get(i).getDescription());
                }
                Messages.COMMAND_HEAD.send(player);
            }
        }

        return true;
    }

    public static ArrayList<CommandTemplate> getSubCommands() {
        return subcommands;
    }
}
