package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.island.IslandAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

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

        SkyFactionsReborn.db.getPlayerIsland(player).thenAccept(island -> {
            if (island == null) {
                Messages.NO_ISLAND.send(player);
            } else {
                World world = Bukkit.getWorld(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Island.ISLAND_WORLD_NAME"));
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

    @Override
    public String permission() {
        return "skyfactions.island.teleport";
    }
}