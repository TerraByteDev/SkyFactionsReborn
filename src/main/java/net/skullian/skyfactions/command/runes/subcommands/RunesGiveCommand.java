package net.skullian.skyfactions.command.runes.subcommands;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CooldownHandler;
import net.skullian.skyfactions.command.PermissionsHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class RunesGiveCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives other players runes (ADMIN COMMAND)";
    }

    @Override
    public String getSyntax() {
        return "/runes give <type> <Player name / Faction name> <amount>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;
        if (args.length < 4) {
            Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
        } else {

            if (args[1].equalsIgnoreCase("player")) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);
                if (!offlinePlayer.hasPlayedBefore()) {
                    Messages.UNKNOWN_PLAYER.send(player, "%player%", args[2]);
                } else {
                    IslandAPI.hasIsland(player).whenComplete((hasIsland, ex) -> {
                        if (ex != null) {
                            ErrorHandler.handleError(player, "check if you had an island", "SQL_ISLAND_GET", ex);
                            return;
                        } else if (!hasIsland) {
                            Messages.NO_ISLAND.send(player);
                            return;
                        }

                        RunesAPI.addRunes(offlinePlayer.getUniqueId(), Integer.parseInt(args[3])).whenComplete((ignored, throwable) -> {
                            if (throwable != null) {
                                throwable.printStackTrace();
                                ErrorHandler.handleError(player, "give runes to another player", "SQL_RUNES_GIVE", throwable);
                                return;
                            }

                            Messages.RUNES_GIVE_SUCCESS.send(player, "%amount%", args[3], "%name%", offlinePlayer.getName());
                        });
                    });
                }
            } else if (args[1].equalsIgnoreCase("faction")) {

                String factionName = args[2];
                FactionAPI.getFaction(factionName).whenComplete((faction, throwable) -> {
                    if (throwable != null) {
                        ErrorHandler.handleError(player, "get the specified Faction", "SQL_FACTION_GET", throwable);
                        return;
                    } else if (faction == null) {
                        Messages.FACTION_NOT_FOUND.send(player, "%name%", factionName);
                        return;
                    }

                    faction.addRunes(Integer.parseInt(args[3])).whenComplete((ignored, ex) -> {
                        if (ex != null) {
                            ErrorHandler.handleError(player, "give Runes to the specified Faction", "SQL_RUNES_MODIFY", ex);
                            return;
                        }

                        Messages.RUNES_GIVE_SUCCESS.send(player, "%amount%", args[3], "%name%", faction.getName());
                    });
                });

            } else {
                Messages.INCORRECT_USAGE.send(player, "%usage%", getSyntax());
            }

        }
    }

    public static List<String> permissions = List.of("skyfactions.runes.give");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
