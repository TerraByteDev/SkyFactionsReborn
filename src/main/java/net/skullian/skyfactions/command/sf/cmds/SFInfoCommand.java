package net.skullian.skyfactions.command.sf.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public void perform(Player player, String[] args) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        player.sendMessage(TextUtility.color(
                "&3Version: &r{#15FB08}" + SkyFactionsReborn.getInstance().getDescription().getVersion() + "{/#00B0CA}&r\n" +
                        "&3Authors: &r{#15FB08}" + SkyFactionsReborn.getInstance().getDescription().getAuthors() + "{/#00B0CA}&r\n" +
                        "&3Discord: &r{#15FB08}" + SkyFactionsReborn.getInstance().getDescription().getWebsite() + "{/#00B0CA}&r"
        ));
        Messages.COMMAND_HEAD.send(player);
    }

    public static List<String> permissions = List.of("skyfactions.command.info");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
