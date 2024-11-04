package net.skullian.skyfactions.command.island.cmds;

import java.util.List;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.screens.confirmation.IslandCreationConfirmationUI;
import net.skullian.skyfactions.util.ErrorUtil;

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
    @Permission(value = { "skyfactions.island.create", "skyfactions.island" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        IslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "create an island", "SQL_ISLAND_CHECK", ex);
                return;
            }

            if (hasIsland) {
                Messages.ISLAND_CREATION_DENY.send(player, PlayerHandler.getLocale(player.getUniqueId()));
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
