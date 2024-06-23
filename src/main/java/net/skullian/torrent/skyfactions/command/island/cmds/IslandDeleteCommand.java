package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.island.IslandAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class IslandDeleteCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete your island. PERMANENT.";
    }

    @Override
    public String getSyntax() {
        return "/island delete <confirm>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        SkyFactionsReborn.db.hasIsland(player).thenAccept(has -> {
            if (!has) {
                Messages.NO_ISLAND.send(player);
            } else {
                // they did /island delete <SOMETHING HERE>
                if (args.length >= 1) {
                    if (args[1].equals("confirm")) {

                        if (IslandAPI.awaitingDeletion.contains(player.getUniqueId())) {
                            World hubWorld = Bukkit.getWorld(SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getString("Hub.WORLD_NAME"));
                            if (hubWorld != null) {
                                List<Integer> hubLocArray = SkyFactionsReborn.configHandler.SETTINGS_CONFIG.getIntegerList("Hub.HUB_LOCATION");
                                Location location = new Location(hubWorld, hubLocArray.get(0), hubLocArray.get(1), hubLocArray.get(2));

                                IslandAPI.teleportPlayerToLocation(player, location);

                                IslandAPI.awaitingDeletion.remove(player);
                                SkyFactionsReborn.db.removeIsland(player).thenAccept(res -> {
                                    Messages.ISLAND_DELETION_SUCCESS.send(player);
                                }).exceptionally(ex -> {
                                    ex.printStackTrace();
                                    Messages.ERROR.send(player, "%operation%", "delete your island", "%debug%", "SQL_ISLAND_DELETE");
                                    return null;
                                });
                            } else {
                                Messages.ERROR.send(player, "%operation%", "delete your island", "%debug%", "WORLD_NOT_EXIST");
                            }
                        }

                    } else {
                        Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
                    }
                } else {
                    IslandAPI.awaitingDeletion.add(player.getUniqueId());
                    Messages.DELETION_CONFIRM.send(player);
                }
            }
        });
    }

    @Override
    public String permission() {
        return "skyfactions.island.delete";
    }
}
