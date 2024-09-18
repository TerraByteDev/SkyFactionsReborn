package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class IslandUntrustCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "untrust";
    }

    @Override
    public String getDescription() {
        return "Untrust a player, so they can not longer visit";
    }

    @Override
    public String getSyntax() {
        return "/untrust <player name>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                Messages.UNKNOWN_PLAYER.send(player, "%player%", args[1]);
                return;
            }

            SkyFactionsReborn.db.getPlayerIsland(player.getUniqueId()).thenAccept(is -> {
                if (is == null) {
                    Messages.NO_ISLAND.send(player);
                } else {
                    SkyFactionsReborn.db.isPlayerTrusted(target.getPlayer(), is.getId()).thenAccept(isTrusted -> {

                        if (isTrusted) {
                            SkyFactionsReborn.db.removeTrust(target.getPlayer(), is.getId()).thenAccept(result -> {
                                Messages.UNTRUST_SUCCESS.send(player, "%player%", target.getName());
                            }).exceptionally(ex -> {
                                ex.printStackTrace();
                                Messages.ERROR.send(player, "%operation%", "untrust a player", "%debug%", "SQL_TRUST_REMOVE");
                                return null;
                            });
                        } else {
                            Messages.UNTRUST_FAILURE.send(player);
                        }

                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        Messages.ERROR.send(player, "%operation%", "untrust a player", "%debug%", "SQL_TRUST_GET");
                        return null;
                    });

                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "untrust a player", "%debug%", "SQL_ISLAND_GET");
                return null;
            });

        } else {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        }
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.island.untrust", "skyfactions.island", "skyfactions.player");
    }
}
