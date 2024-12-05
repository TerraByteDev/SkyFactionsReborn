package net.skullian.skyfactions.common.command.sf.cmds;

import java.util.List;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.util.text.TextUtility;

@Command("sf")
public class SFInfoCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "sf";
    }

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
            SkyUser sender
    ) {
        if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, permission(), true)) return;
        String locale = sender.isConsole() ? SkyApi.getInstance().getPlayerAPI().getLocale(sender.getUniqueId()) : Messages.getDefaulLocale();

        Messages.COMMAND_HEAD.send(sender, locale);
        sender.sendMessage(TextUtility.color(
                "<dark_aqua>Version: <reset><gray>" + SkyApi.getInstance().getPluginInfoAPI().getVersion() + "</gradient><reset>\n" +
                        "<dark_aqua>Authors: <reset><gray>" + SkyApi.getInstance().getPluginInfoAPI().getAuthors().replace("[", "").replace("]", "") + "</gradient><reset>\n" +
                        "<dark_aqua>Website: <reset><gray>" + SkyApi.getInstance().getPluginInfoAPI().getWebsite() + "</gradient><reset>\n" +
                        "<dark_aqua>Contributors: <reset><gray>" + SkyApi.getInstance().getPluginInfoAPI().getContributors().replace("[", "").replace("]", "") + "</gradient><reset>",
                        locale,
                        sender
        ));
        Messages.COMMAND_HEAD.send(sender, locale);
    }

    public static List<String> permissions = List.of("skyfactions.sf.info");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
