package net.skullian.skyfactions.command.island;

import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.island.cmds.*;
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

public class IslandCommandTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length == 0 || !sender.hasPermission("skyfactions.island.help")) {
            return null;
        }

        if (args.length == 1) {
            String arg = args[0].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();


            if (CommandsUtility.hasPerm((Player) sender, IslandHelpCommand.permissions, false)) {
                completions.add("help");
            }
            if (CommandsUtility.hasPerm((Player) sender, IslandCreateCommand.permissions, false)) {
                completions.add("create");
            }
            if (CommandsUtility.hasPerm((Player) sender, IslandTeleportCommand.permissions, false)) {
                completions.add("teleport");
            }
            if (CommandsUtility.hasPerm((Player) sender, IslandDeleteCommand.permissions, false)) {
                completions.add("delete");
            }
            if (CommandsUtility.hasPerm((Player) sender, IslandTrustCommand.permissions, false)) {
                completions.add("trust");
            }
            if (CommandsUtility.hasPerm((Player) sender, IslandUntrustCommand.permissions, false)) {
                completions.add("untrust");
            }
            if (CommandsUtility.hasPerm((Player) sender, IslandVisitCommand.permissions, false)) {
                completions.add("visit");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        } else if (args.length == 2) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String arg = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (subcmd.equals("delete") && CommandsUtility.hasPerm((Player) sender, IslandDeleteCommand.permissions, false)) {
                completions.add("confirm");
            }
            if (subcmd.equals("trust") && CommandsUtility.hasPerm((Player) sender, IslandTrustCommand.permissions, false)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            if (subcmd.equals("untrust") && CommandsUtility.hasPerm((Player) sender, IslandUntrustCommand.permissions, false)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            if (subcmd.equals("visit") && CommandsUtility.hasPerm((Player) sender, IslandVisitCommand.permissions, false)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }
        return Arrays.asList("");
    }
}
