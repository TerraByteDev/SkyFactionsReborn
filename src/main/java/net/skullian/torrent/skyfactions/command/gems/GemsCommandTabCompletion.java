package net.skullian.torrent.skyfactions.command.gems;

import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.command.gems.cmds.GemsBalanceCommand;
import net.skullian.torrent.skyfactions.command.gems.cmds.GemsGiveCommand;
import net.skullian.torrent.skyfactions.command.gems.cmds.GemsHelpCommand;
import net.skullian.torrent.skyfactions.command.gems.cmds.GemsPayCommand;
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

            if (PermissionsHandler.hasPerm((Player) sender, GemsHelpCommand.permissions, false)) {
                completions.add("help");
            }
            if (PermissionsHandler.hasPerm((Player) sender, GemsPayCommand.permissions, false)) {
                completions.add("pay");
            }
            if (PermissionsHandler.hasPerm((Player) sender, GemsBalanceCommand.permissions, false)) {
                completions.add("balance");
            }
            if (PermissionsHandler.hasPerm((Player) sender, GemsGiveCommand.permissions, false)) {
                completions.add("give");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }else if (args.length == 2) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String arg = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (PermissionsHandler.hasPerm((Player) sender, GemsPayCommand.permissions, false) && subcmd.equals("pay")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            if (PermissionsHandler.hasPerm((Player) sender, GemsGiveCommand.permissions, false) && subcmd.equals("give")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }

        return Arrays.asList("");
    }


}
