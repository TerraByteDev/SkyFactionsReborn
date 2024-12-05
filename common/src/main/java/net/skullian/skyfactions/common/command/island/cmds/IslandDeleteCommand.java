package net.skullian.skyfactions.common.command.island.cmds;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Command("island")
public class IslandDeleteCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "island";
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete your island permanently.";
    }

    @Override
    public String getSyntax() {
        return "/island delete <confirm>";
    }

    @Command("delete [confirm]")
    @Permission(value = {"skyfactions.island.delete", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "confirm") @Nullable String confirm
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "check if you have an island", "SQL_ISLAND_CHECK", ex);
                return;
            } else if (island == null) {
                Messages.NO_ISLAND.send(player, locale);
                return;
            }

            if (confirm == null) {
                Messages.DELETION_CONFIRM.send(player, locale);
                SkyApi.getInstance().getIslandAPI().getAwaitingDeletion().add(player.getUniqueId());
            } else if (confirm.equalsIgnoreCase("confirm")) {

                if (SkyApi.getInstance().getIslandAPI().getAwaitingDeletion().contains(player.getUniqueId())) {
                    String hubWorld = Settings.HUB_WORLD_NAME.getString();
                    // todo migrate to gui command conf
                    if (SkyApi.getInstance().getRegionAPI().worldExists(hubWorld)) {
                        Messages.DELETION_PROCESSING.send(player, locale);

                        SkyApi.getInstance().getIslandAPI().onIslandRemove(player);
                    } else {
                        Messages.ERROR.send(player, locale, "operation", "delete your island", "debug", "WORLD_NOT_EXIST");
                    }
                } else {
                    Messages.DELETION_BLOCK.send(player, locale);
                }
            } else {
                Messages.INCORRECT_USAGE.send(player, locale, "usage", getSyntax());
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.delete", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
