package net.skullian.skyfactions.command.sf;

import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.command.sf.cmds.SFHelpCommand;
import net.skullian.skyfactions.command.sf.cmds.SFInfoCommand;
import net.skullian.skyfactions.command.sf.cmds.SFReloadCommand;
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

public class SFCommandTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player) || args.length == 0 || !sender.hasPermission("skyfactions.sf.help")) {
            return null;
        }

        if (args.length == 1) {
            String arg = args[0].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();

            if (PermissionsHandler.hasPerm((Player) sender, SFHelpCommand.permissions, false)) {
                completions.add("help");
            }
            if (PermissionsHandler.hasPerm((Player) sender, SFReloadCommand.permissions, false)) {
                completions.add("reload");
            }
            if (PermissionsHandler.hasPerm((Player) sender, SFInfoCommand.permissions, false)) {
                completions.add("info");
            }

            return StringUtil.copyPartialMatches(arg, completions, new ArrayList<>(completions.size()));
        }
        return Arrays.asList("");
    }
}
