package net.skullian.torrent.skyfactions.command.island;

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

            if (sender.hasPermission("skyfactions.island.help")) {
                completions.add("help");
            }
            if (sender.hasPermission("skyfactions.island.create")) {
                completions.add("create");
            }
            if (sender.hasPermission("skyfactions.island.teleport")) {
                completions.add("teleport");
            }
            if (sender.hasPermission("skyfactions.island.delete")) {
                completions.add("delete");
            }
            if (sender.hasPermission("skyfactions.island.trust")) {
                completions.add("trust");
            }
            if (sender.hasPermission("skyfactions.island.untrust")) {
                completions.add("untrust");
            }
            if (sender.hasPermission("skyfactions.island.visit")) {
                completions.add("visit");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        } else if (args.length == 2) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String arg = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (subcmd.equals("delete") && sender.hasPermission("skyfactions.island.delete")) {
                completions.add("confirm");
            }
            if (subcmd.equals("trust") && sender.hasPermission("skyfactions.island.trust")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            if (subcmd.equals("untrust") && sender.hasPermission("skyfactions.island.untrust")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            if (subcmd.equals("visit") && sender.hasPermission("skyfactions.island.visit")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }
        return Arrays.asList("");
    }
}
