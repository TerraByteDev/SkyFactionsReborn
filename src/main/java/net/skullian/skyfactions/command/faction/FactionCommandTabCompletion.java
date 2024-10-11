package net.skullian.skyfactions.command.faction;

import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.command.faction.cmds.*;
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

            if (PermissionsHandler.hasPerm((Player)  sender, FactionHelpCommand.permissions, false)) {
                completions.add("help");
            }
            if (PermissionsHandler.hasPerm((Player) sender, FactionCreateCommand.permissions, false)) {
                completions.add("create");
            }
            if (PermissionsHandler.hasPerm((Player) sender, FactionInfoCommand.permissions, false)) {
                completions.add("info");
            }
            if (PermissionsHandler.hasPerm((Player) sender, FactionTeleportCommand.permissions, false)) {
                completions.add("teleport");
            }
            if (PermissionsHandler.hasPerm((Player) sender, FactionMOTDCommand.permissions, false)) {
                completions.add("motd");
            }
            if (PermissionsHandler.hasPerm((Player) sender, FactionLeaveCommand.permissions, false)) {
                completions.add("leave");
            }
            if (PermissionsHandler.hasPerm((Player) sender, FactionBroadcastCommand.permissions, false)) {
                completions.add("broadcast");
            }
            if (PermissionsHandler.hasPerm((Player) sender, FactionInviteCommand.permissions, false)) {
                completions.add("invite");
            }
            if (PermissionsHandler.hasPerm((Player) sender, FactionRequestJoinCommand.permissions, false)) {
                completions.add("requestjoin");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        } else if (args.length == 2) {
            String subcmd = args[0].toLowerCase(Locale.ROOT);
            String arg = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (subcmd.equals("invite") && PermissionsHandler.hasPerm((Player) sender, FactionInviteCommand.permissions, false)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }
        return Arrays.asList("");
    }
}
