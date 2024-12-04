package net.skullian.skyfactions.paper.command.island.cmds;

import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.gui.screens.confirmation.IslandCreationConfirmationUI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("island")
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

    @Command("create")
    @Permission(value = {"skyfactions.island.create", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SpigotIslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create an island", "SQL_ISLAND_CHECK", ex);
                return;
            }

            if (hasIsland) {
                Messages.ISLAND_CREATION_DENY.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
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
