package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.api.IslandAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        SkyFactionsReborn.db.getPlayerIsland(player.getUniqueId()).thenAccept(island -> {
            if (island == null) {
                Messages.NO_ISLAND.send(player);
            } else {
                World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                if (world != null) {
                    IslandAPI.teleportPlayerToLocation(player, island.getCenter(world));
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

    public static List<String> permissions = List.of("skyfactions.island.teleport", "skyfactions.island", "skyfactions.player");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
