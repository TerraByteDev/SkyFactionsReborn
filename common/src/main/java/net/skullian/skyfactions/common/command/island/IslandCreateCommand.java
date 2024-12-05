package net.skullian.skyfactions.common.command.island;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.screens.confirmation.IslandCreationConfirmationUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("island")
public class IslandCreateCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "island";
    }

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
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create an island", "SQL_ISLAND_CHECK", ex);
                return;
            }

            if (island != null) {
                Messages.ISLAND_CREATION_DENY.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
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
