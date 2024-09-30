package net.skullian.torrent.skyfactions.command.runes;

import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.command.runes.subcommands.RunesBalanceCommand;
import net.skullian.torrent.skyfactions.command.runes.subcommands.RunesGiveCommand;
import net.skullian.torrent.skyfactions.command.runes.subcommands.RunesHelpCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RunesCommandTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length == 0 || !sender.hasPermission("skyfactions.island.help")) {
            return null;
        }

        if (args.length == 1) {
            String arg = args[0].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (PermissionsHandler.hasPerm((Player) sender, RunesHelpCommand.permissions, false)) {
                completions.add("help");
            }
            if (PermissionsHandler.hasPerm((Player) sender, RunesBalanceCommand.permissions, false)) {
                completions.add("balance");
            }
            if (PermissionsHandler.hasPerm((Player) sender, RunesGiveCommand.permissions, false)) {
                completions.add("give");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        } else if (args.length == 2) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String arg = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (PermissionsHandler.hasPerm((Player) sender, RunesGiveCommand.permissions, false) && subcmd.equals("give")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }

        return Arrays.asList("");
    }

}
