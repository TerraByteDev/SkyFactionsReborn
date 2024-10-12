package net.skullian.skyfactions.command.runes;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.command.runes.subcommands.RunesBalanceCommand;
import net.skullian.skyfactions.command.runes.subcommands.RunesGiveCommand;
import net.skullian.skyfactions.command.runes.subcommands.RunesHelpCommand;
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
                completions.add("faction");
                completions.add("player");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        } else if (args.length == 3) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String type = args[1].toLowerCase(Locale.ROOT);
            String arg = args[2].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (PermissionsHandler.hasPerm((Player) sender, RunesGiveCommand.permissions, false) && subcmd.equals("give")) {
                if (type.equalsIgnoreCase("faction")) {
                    for (String name : FactionAPI.factionNameCache.keySet()) {
                        completions.add(name);
                    }
                } else if (type.equalsIgnoreCase("player")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                }
            }
        }

        return Arrays.asList("");
    }

}
