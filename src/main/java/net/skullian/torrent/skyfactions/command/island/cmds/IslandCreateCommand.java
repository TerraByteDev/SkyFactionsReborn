package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.api.IslandAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.gui.IslandCreationConfirmationUI;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class IslandCreateCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a brand new island!";
    }

    @Override
    public String getSyntax() {
        return "/island create";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        IslandAPI.hasIsland(player).whenComplete((hasIsland, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "create an island", "SQL_ISLAND_CHECK", ex);
                return;
            }

            if (hasIsland) {
                Messages.ISLAND_CREATION_DENY.send(player);
            } else {
                IslandCreationConfirmationUI.promptPlayer(player);
            }
        });
        
    }

    public static List<String> permissions = List.of("skyfactions.island.create", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
