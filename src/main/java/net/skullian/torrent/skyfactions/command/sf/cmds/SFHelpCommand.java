package net.skullian.torrent.skyfactions.command.sf.cmds;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.command.raid.RaidCommandHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.entity.Player;

public class SFHelpCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Get help on all general SF commands.";
    }

    @Override
    public String getSyntax() {
        return "/sf help";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        Messages.COMMAND_HEAD.send(player);
        if (RaidCommandHandler.getSubCommands().size() <= 0) {
            Messages.NO_COMMANDS_FOUND.send(player);
        }
        for (int i = 0; i < RaidCommandHandler.getSubCommands().size(); i++) {
            if (!PermissionsHandler.hasPerm(player, RaidCommandHandler.getSubCommands().get(i).permission(), false)) continue;
            Messages.COMMAND_INFO.send(player, "%command_syntax%", RaidCommandHandler.getSubCommands().get(i).getSyntax(), "%command_name%", RaidCommandHandler.getSubCommands().get(i).getName(), "%command_description%", RaidCommandHandler.getSubCommands().get(i).getDescription());
        }
        Messages.COMMAND_HEAD.send(player);
    }

    @Override
    public String permission() {
        return "skyfactions.sf.help";
    }
}
