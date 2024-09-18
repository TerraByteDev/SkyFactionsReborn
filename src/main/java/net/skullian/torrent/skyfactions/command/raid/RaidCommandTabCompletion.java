package net.skullian.torrent.skyfactions.command.raid;

import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.command.raid.cmds.RaidHelpCommand;
import net.skullian.torrent.skyfactions.command.raid.cmds.RaidResetCooldown;
import net.skullian.torrent.skyfactions.command.raid.cmds.RaidStartCommand;
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

public class RaidCommandTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player) || args.length == 0 || !sender.hasPermission("skyfactions.raid.help")) {
            return null;
        }

        if (args.length == 1) {
            String arg = args[0].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (PermissionsHandler.hasPerm((Player) sender, RaidHelpCommand.permissions, false)) {
                completions.add("help");
            }
            if (PermissionsHandler.hasPerm((Player) sender, RaidStartCommand.permissions, false)) {
                completions.add("start");
            }
            if (PermissionsHandler.hasPerm((Player) sender, RaidResetCooldown.permissions, false)) {
                completions.add("resetcooldown");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }
        return Arrays.asList("");
    }

}
