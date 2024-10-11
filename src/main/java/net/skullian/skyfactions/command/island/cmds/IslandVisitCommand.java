package net.skullian.skyfactions.command.island.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RaidAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

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
                Messages.UNKNOWN_PLAYER.send(player, "%player%", args[1]);
                return;
            }

            SkyFactionsReborn.db.getPlayerIsland(target.getUniqueId()).thenAccept(is -> {
                if (is != null) {

                    if (RaidAPI.currentRaids.containsValue(player.getUniqueId()) || RaidAPI.processingRaid.containsValue(player.getUniqueId())) {
                        Messages.VISIT_IN_RAID.send(player);
                    } else {
                        SkyFactionsReborn.db.isPlayerTrusted(player, is.getId()).thenAccept(isTrusted -> {
                            World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                            if (world == null) {
                                Messages.ERROR.send(player, "%operation%", "visit a player", "%debug%", "WORLD_NOT_EXIST");
                            } else {
                                if (isTrusted) {
                                    Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
                                        IslandAPI.handlePlayerJoinBorder(player, is); // shift the worldborder
                                        IslandAPI.teleportPlayerToLocation(player, is.getCenter(world));
                                    });
                                } else {
                                    Messages.PLAYER_NOT_TRUSTED.send(player);
                                }
                            }

                        }).exceptionally(ex -> {
                            ex.printStackTrace();
                            Messages.ERROR.send(player, "%operation%", "visit a player", "%debug%", "SQL_TRUST_GET");
                            return null;
                        });
                    }

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

    public static List<String> permissions = List.of("skyfactions.island.visit", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
