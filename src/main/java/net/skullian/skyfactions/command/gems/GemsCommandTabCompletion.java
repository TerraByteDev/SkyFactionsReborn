package net.skullian.skyfactions.command.gems;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.gems.cmds.GemsBalanceCommand;
import net.skullian.skyfactions.command.gems.cmds.GemsGiveCommand;
import net.skullian.skyfactions.command.gems.cmds.GemsHelpCommand;
import net.skullian.skyfactions.command.gems.cmds.GemsPayCommand;
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

public class GemsCommandTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length == 0 || !sender.hasPermission("skyfactions.island.help")) {
            return null;
        }

        if (args.length == 1) {
            String arg = args[0].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (CommandsUtility.hasPerm((Player) sender, GemsHelpCommand.permissions, false)) {
                completions.add("help");
            }
            if (CommandsUtility.hasPerm((Player) sender, GemsPayCommand.permissions, false)) {
                completions.add("pay");
            }
            if (CommandsUtility.hasPerm((Player) sender, GemsBalanceCommand.permissions, false)) {
                completions.add("balance");
            }
            if (CommandsUtility.hasPerm((Player) sender, GemsGiveCommand.permissions, false)) {
                completions.add("give");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        } else if (args.length == 2) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String arg = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (CommandsUtility.hasPerm((Player) sender, GemsPayCommand.permissions, false) && subcmd.equals("pay")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            if (CommandsUtility.hasPerm((Player) sender, GemsGiveCommand.permissions, false) && subcmd.equals("give")) {
                completions.add("faction");
                completions.add("player");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        } else if (args.length == 3) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String type = args[1].toLowerCase(Locale.ROOT);
            String arg = args[2].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (CommandsUtility.hasPerm((Player) sender, GemsGiveCommand.permissions, false) && subcmd.equals("give")) {
                if (type.equalsIgnoreCase("faction")) {
                    completions.addAll(FactionAPI.factionNameCache.keySet());
                } else if (type.equalsIgnoreCase("player")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                }
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }

        return Arrays.asList("");
    }


}
