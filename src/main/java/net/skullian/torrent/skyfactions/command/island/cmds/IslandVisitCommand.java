package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.island.IslandAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class IslandVisitCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "visit";
    }

    @Override
    public String getDescription() {
        return "Visit another player's island if you are trusted.";
    }

    @Override
    public String getSyntax() {
        return "/island visit <player>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        if (args.length > 1) {
            Messages.VISIT_PROCESSING.send(player);
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                Messages.UNKNOWN_PLAYER.send(player);
                return;
            }

            SkyFactionsReborn.db.getPlayerIsland(target.getPlayer()).thenAccept(is -> {
                if (is != null) {

                    SkyFactionsReborn.db.isPlayerTrusted(player, is.getId()).thenAccept(isTrusted -> {
                        World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                        if (world == null) {
                            Messages.ERROR.send(player, "%operation%", "visit a player", "%debug%", "WORLD_NOT_EXIST");
                        } else {
                            if (isTrusted) {
                                IslandAPI.teleportPlayerToLocation(player, is.getCenter(world));
                            } else {
                                Messages.PLAYER_NOT_TRUSTED.send(player);
                            }
                        }

                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        Messages.ERROR.send(player, "%operation%", "visit a player", "%debug%", "SQL_TRUST_GET");
                        return null;
                    });

                } else {
                    Messages.VISIT_NO_ISLAND.send(player);
                }

            }).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "visit a player", "%debug%", "SQL_ISLAND_GET");
                return null;
            });
        }
    }

    @Override
    public String permission() {
        return "skyfactions.island.visit";
    }
}
