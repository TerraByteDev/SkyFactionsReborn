package net.skullian.skyfactions.command.runes.subcommands;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;
import java.util.stream.Collectors;

@Command("runes")
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
        return "/runes give <type> <Player Name / Faction Name> <amount>";
    }

    @Suggestions("giveTypeSelection")
    public List<String> selectionSuggestion(CommandContext<CommandSender> context, CommandInput input) {
        return List.of("player", "faction");
    }

    @Suggestions("playerFactionName")
    public List<String> suggestPlayers(CommandContext<CommandSender> context, CommandInput input) {
        System.out.println(input.input());
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Command("give <type> <playerFactionName> <amount> ")
    public void perform(
            CommandSender sender,
            @Argument(value = "type", suggestions = "giveTypeSelection") String type,
            @Argument(value = "playerFactionName", suggestions = "playerFactionName") String playerFactionName,
            @Argument(value = "amount") int amount

    ) {
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        if ((sender instanceof Player) && CommandsUtility.manageCooldown((Player) sender)) return;
            if (type.equalsIgnoreCase("player")) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerFactionName);
                if (!offlinePlayer.hasPlayedBefore()) {
                    Messages.UNKNOWN_PLAYER.send(sender, "%player%", playerFactionName);
                } else {
                    IslandAPI.hasIsland(offlinePlayer.getUniqueId()).whenComplete((hasIsland, ex) -> {
                        if (ex != null) {
                            ErrorHandler.handleError(sender, "check if the player had an island", "SQL_ISLAND_GET", ex);
                            return;
                        } else if (!hasIsland) {
                            Messages.PLAYER_HAS_NO_ISLAND.send(sender);
                            return;
                        }

                        RunesAPI.addRunes(offlinePlayer.getUniqueId(), amount).whenComplete((ignored, throwable) -> {
                            if (throwable != null) {
                                throwable.printStackTrace();
                                ErrorHandler.handleError(sender, "give runes to another player", "SQL_RUNES_GIVE", throwable);
                                return;
                            }

                            Messages.RUNES_GIVE_SUCCESS.send(sender, "%amount%", amount, "%name%", offlinePlayer.getName());
                        });
                    });
                }
            } else if (type.equalsIgnoreCase("faction")) {

                FactionAPI.getFaction(playerFactionName).whenComplete((faction, throwable) -> {
                    if (throwable != null) {
                        ErrorHandler.handleError(sender, "get the specified Faction", "SQL_FACTION_GET", throwable);
                        return;
                    } else if (faction == null) {
                        Messages.FACTION_NOT_FOUND.send(sender, "%name%", playerFactionName);
                        return;
                    }

                    faction.addRunes(amount).whenComplete((ignored, ex) -> {
                        if (ex != null) {
                            ErrorHandler.handleError(sender, "give Runes to the specified Faction", "SQL_RUNES_MODIFY", ex);
                            return;
                        }

                        Messages.RUNES_GIVE_SUCCESS.send(sender, "%amount%", amount, "%name%", faction.getName());
                    });
                });

        }
    }

    public static List<String> permissions = List.of("skyfactions.runes.give");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
