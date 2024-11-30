package net.skullian.skyfactions.paper.command.sf.cmds;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.common.util.text.TextUtility;

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
    @Permission(value = { "skyfactions.sf.info" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSender sender
    ) {
        
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        String locale = sender instanceof Player ? ((Player) sender).locale().getLanguage() : Messages.getDefaulLocale();

        Messages.COMMAND_HEAD.send(sender, locale);
        sender.sendMessage(TextUtility.color(
                "<dark_aqua>Version: <reset><gray>" + SkyFactionsReborn.getInstance().getDescription().getVersion() + "</gradient><reset>\n" +
                        "<dark_aqua>Authors: <reset><gray>" + SkyFactionsReborn.getInstance().getDescription().getAuthors().toString().replace("[", "").replace("]", "") + "</gradient><reset>\n" +
                        "<dark_aqua>Website: <reset><gray>" + SkyFactionsReborn.getInstance().getDescription().getWebsite() + "</gradient><reset>\n" +
                        "<dark_aqua>Contributors: <reset><gray>" + SkyFactionsReborn.getInstance().getDescription().getContributors().toString().replace("[", "").replace("]", "") + "</gradient><reset>",
                        locale,
                        sender instanceof Player ? ((Player) sender) : null
        ));
        Messages.COMMAND_HEAD.send(sender, locale);
    }

    public static List<String> permissions = List.of("skyfactions.sf.info");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
