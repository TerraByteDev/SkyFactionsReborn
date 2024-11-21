package net.skullian.skyfactions.command.faction.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.screens.confirmation.FactionLeaveConfirmationUI;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.PlayerSource;

import java.util.List;

@Command("faction")
public class FactionLeaveCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave the Faction you are currently in.";
    }

    @Override
    public String getSyntax() {
        return "/faction leave";
    }

    @Command("leave")
    @Permission(value = {"skyfactions.faction.leave", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            }

            FactionLeaveConfirmationUI.promptPlayer(player);
        });

    }

    public static List<String> permissions = List.of("skyfactions.faction.leave", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
