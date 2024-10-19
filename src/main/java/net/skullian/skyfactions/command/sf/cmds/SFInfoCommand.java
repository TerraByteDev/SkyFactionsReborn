package net.skullian.skyfactions.command.sf.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.text.TextUtility;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("sf")
public class SFInfoCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Gives you information concerning SkyFactions.";
    }

    @Override
    public String getSyntax() {
        return "/sf info";
    }

    @Command("info")
    @Permission(value = { "skyfactions.command.info" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        if ((sender instanceof Player) && CommandsUtility.manageCooldown((Player) sender)) return;

        Messages.COMMAND_HEAD.send(sender);
        sender.sendMessage(TextUtility.color(
                "&3Version: &r{#15FB08}" + SkyFactionsReborn.getInstance().getDescription().getVersion() + "{/#00B0CA}&r\n" +
                        "&3Authors: &r{#15FB08}" + SkyFactionsReborn.getInstance().getDescription().getAuthors().toString().replace("[", "").replace("]", "") + "{/#00B0CA}&r\n" +
                        "&3Discord: &r{#15FB08}" + SkyFactionsReborn.getInstance().getDescription().getWebsite() + "{/#00B0CA}&r\n" +
                        "&3Contributors: &r{#15FB08}" + SkyFactionsReborn.getInstance().getDescription().getContributors().toString().replace("[", "").replace("]", "") + "{/#00B0CA}&r"
        ));
        Messages.COMMAND_HEAD.send(sender);
    }

    public static List<String> permissions = List.of("skyfactions.command.info");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
