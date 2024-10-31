package net.skullian.skyfactions.command.island.cmds;

import java.util.List;

import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.util.ErrorHandler;

@Command("island")
public class IslandTeleportCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDescription() {
        return "Teleport to your player island.";
    }

    @Override
    public String getSyntax() {
        return "/island teleport";
    }

    @Command("teleport")
    @Permission(value = { "skyfactions.island.teleport", "skyfactions.island" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (island == null) {
                Messages.NO_ISLAND.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            } else if (FactionAPI.isLocationInRegion(player.getLocation(), player.getUniqueId().toString())) {
                Messages.ALREADY_ON_ISLAND.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            }

            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
            if (world != null) {
                FactionAPI.modifyDefenceOperation(FactionAPI.DefenceOperation.DISABLE, player);

                IslandAPI.handlePlayerJoinBorder(player, island); // shift join border
                IslandAPI.teleportPlayerToLocation(player, island.getCenter(world));

                IslandAPI.onIslandLoad(player.getUniqueId());
            } else {
                Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "teleport you to your island", "debug", "WORLD_NOT_EXIST");
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.teleport", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
