package net.skullian.torrent.skyfactions.command.faction;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FactionCommandTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player) || args.length == 0 || !sender.hasPermission("skyfactions.faction.help")) {
            return null;
        }

        if (args.length == 1) {
            String arg = args[0].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (sender.hasPermission("skyfactions.faction.help")) {
                completions.add("help");
            }
            if (sender.hasPermission("skyfactions.faction.create")) {
                completions.add("create");
            }
            if (sender.hasPermission("skyfactions.faction.info")) {
                completions.add("info");
            }
            if (sender.hasPermission("skyfactions.faction.teleport")) {
                completions.add("teleport");
            }
            if (sender.hasPermission("skyfactions.faction.motd")) {
                completions.add("motd");
            }
            if (sender.hasPermission("skyfactions.faction.leave")) {
                completions.add("leave");
            }
            if (sender.hasPermission("skyfactions.faction.broadcast")) {
                completions.add("broadcast");
            }
            if (sender.hasPermission("skyfactions.faction.invite")) {
                completions.add("invite");
            }
            if (sender.hasPermission("skyfactions.faction.requestjoin")) {
                completions.add("requestjoin");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        } else if (args.length == 2) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String arg = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (subcmd.equals("invite") && sender.hasPermission("skyfactions.faction.invite")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }
        return Arrays.asList("");
    }
}
