package net.skullian.skyfactions.command.island.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

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
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (!target.hasPlayedBefore()) {
                Messages.UNKNOWN_PLAYER.send(player, "%player%", args[1]);
                return;
            }

            SkyFactionsReborn.databaseHandler.getPlayerIsland(player.getUniqueId()).thenAccept(is -> {
                if (is == null) {
                    Messages.NO_ISLAND.send(player);
                } else {
                    SkyFactionsReborn.databaseHandler.isPlayerTrusted(target.getPlayer(), is.getId()).whenComplete((isTrusted, ex) -> {
                        if (ex != null) {
                            ErrorHandler.handleError(player, "check if a player is trusted", "SQL_TRUST_GET", ex);
                            return;
                        }

                        if (isTrusted) {
                            Messages.PLAYER_ALREADY_TRUSTED.send(player);
                        } else {
                            SkyFactionsReborn.databaseHandler.trustPlayer(target.getPlayer(), is.getId()).whenComplete((result, exc) -> {
                                if (exc != null) {
                                    ErrorHandler.handleError(player, "trust a player", "SQL_TRUST_ADD", exc);
                                    return;
                                }

                                Messages.TRUST_SUCCESS.send(player, "%player%", target.getName());
                            });
                        }
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

    public static List<String> permissions = List.of("skyfactions.island.trust", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
