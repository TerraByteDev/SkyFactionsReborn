package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class IslandTrustCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "trust";
    }

    @Override
    public String getDescription() {
        return "Trust another player to visit your island.";
    }

    @Override
    public String getSyntax() {
        return "/island trust <player name>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                Messages.UNKNOWN_PLAYER.send(player);
                return;
            }

            SkyFactionsReborn.db.getPlayerIsland(player).thenAccept(is -> {
                if (is == null) {
                    Messages.NO_ISLAND.send(player);
                } else { // TODO TEST
                    SkyFactionsReborn.db.isPlayerTrusted(target.getPlayer(), is.getId()).thenAccept(isTrusted -> {
                        if (isTrusted) {
                            Messages.PLAYER_ALREADY_TRUSTED.send(player);
                        } else {

                            SkyFactionsReborn.db.trustPlayer(target.getPlayer(), is.getId()).thenAccept(result -> {
                                Messages.TRUST_SUCCESS.send(player, "%player%", target.getName());
                            }).exceptionally(ex -> {
                                ex.printStackTrace();
                                Messages.ERROR.send(player, "%operation%", "trust a player", "%debug%", "SQL_TRUST_ADD");
                                return null;
                            });

                        }
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        Messages.ERROR.send(player, "%operation%", "trust a player", "%debug%", "SQL_TRUST_GET");
                        return null;
                    });

                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "trust a player", "%debug%", "SQL_ISLAND_GET");
                return null;
            });

        } else {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        }
    }

    @Override
    public String permission() {
        return "skyfactions.island.trust";
    }
}
