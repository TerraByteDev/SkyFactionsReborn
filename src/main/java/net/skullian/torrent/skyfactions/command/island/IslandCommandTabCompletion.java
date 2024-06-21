package net.skullian.torrent.skyfactions.command.island;

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
        if (!(sender instanceof Player) || args.length == 0 || !sender.hasPermission("skyfactions.island.command") || !sender.hasPermission("skyfactions.island.help")) {
            return null;
        }

        if (args.length == 1) {
            String arg = args[0].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (sender.hasPermission("skyfactions.command.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("skyfactions.island.info")) {
                completions.add("info");
            }
            if (sender.hasPermission("skyfactions.island.help")) {
                completions.add("help");
            }
            if (sender.hasPermission("skyfactions.island.create")) {
                completions.add("create");
            }
            if (sender.hasPermission("skyfactions.island.teleport")) {
                completions.add("teleport");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }
        return Arrays.asList("");
    }
}
