package net.skullian.skyfactions.command.island.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

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

        SkyFactionsReborn.databaseHandler.getPlayerIsland(player.getUniqueId()).thenAccept(island -> {
            if (island == null) {
                Messages.NO_ISLAND.send(player);
            } else {
                World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                if (world != null) {
                    FactionAPI.modifyDefenceOperation(FactionAPI.DefenceOperation.DISABLE, player);

                    IslandAPI.handlePlayerJoinBorder(player, island); // shift join border
                    IslandAPI.teleportPlayerToLocation(player, island.getCenter(world));

                    IslandAPI.modifyDefenceOperation(FactionAPI.DefenceOperation.ENABLE, player.getUniqueId(), player.getLocation());

                } else {
                    Messages.ERROR.send(player, "%operation%", "teleport you to your island", "%debug%", "WORLD_NOT_EXIST");
                }
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "get your island", "%debug%", "SQL_ISLAND_GET");
            return null;
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.teleport", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
