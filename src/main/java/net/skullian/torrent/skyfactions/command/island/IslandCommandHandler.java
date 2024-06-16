package net.skullian.torrent.skyfactions.command.island;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.command.island.cmds.IslandCreateCommand;
import net.skullian.torrent.skyfactions.command.island.cmds.IslandInfoCommand;
import net.skullian.torrent.skyfactions.command.island.cmds.IslandReloadCommand;
import net.skullian.torrent.skyfactions.command.island.cmds.IslandTest;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class IslandCommandHandler implements CommandExecutor {

    private static ArrayList<CommandTemplate> subcommands = new ArrayList<>();

    public IslandCommandHandler() {
        subcommands.add(new IslandCreateCommand());
        subcommands.add(new IslandReloadCommand());
        subcommands.add(new IslandInfoCommand());
        subcommands.add(new IslandTest());
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
                if (!player.hasPermission("skyfactions.island.command")  || !player.hasPermission("skyfactions.island.help")) {
                    Messages.PERMISSION_DENY.send(player);
                    return true;
                }

                Messages.COMMAND_HEAD.send(player);
                if (getSubCommands().size() <= 0) {
                    Messages.NO_COMMANDS_FOUND.send(player);
                }
                for (int i = 0; i < getSubCommands().size(); i++) {
                    if (!PermissionsHandler.hasPerm(player, getSubCommands().get(i).permission(), false)) continue;
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
