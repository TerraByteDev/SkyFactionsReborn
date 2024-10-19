package net.skullian.skyfactions.command.gems.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.gems.GemsCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Command("gems")
public class GemsGiveCommand extends CommandTemplate{

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Gives other players gems (ADMIN COMMAND)";
    }

    @Override
    public String getSyntax() {
        return "/gems give <player / faction> <Player Name / Faction Name> <amount>";
    }

    @Suggestions("playerFactionName")
    public List<String> suggestPlayers(CommandContext<CommandSourceStack> context, CommandInput input) {
        if (input.input().startsWith("gems give player")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (input.input().startsWith("gems give faction")) {
            return new ArrayList<>(FactionAPI.factionNameCache.keySet());
        }

        return List.of();
    }

    @Suggestions("giveTypeSelection")
    public List<String> selectionSuggestion(CommandContext<CommandSourceStack> context, CommandInput input) {
        return List.of("player", "faction");
    }

    @Command("give <type> <playerFactionName> <amount>")
    @Permission(value = { "skyfactions.gems.give" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack,
            @Argument(value = "type", suggestions = "giveTypeSelection") String type,
            @Argument(value = "playerFactionName", suggestions = "playerFactionName") String playerFactionName,
            @Argument(value = "amount") int amount
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        if ((sender instanceof Player) && CommandsUtility.manageCooldown((Player) sender)) return;


        if (type.equalsIgnoreCase("player")) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerFactionName);
            if (!offlinePlayer.hasPlayedBefore()) {
                Messages.UNKNOWN_PLAYER.send(sender, "%player%", playerFactionName);
            } else {
                IslandAPI.hasIsland(offlinePlayer.getUniqueId()).whenComplete((hasIsland, throwable) -> {
                    if (throwable != null) {
                        ErrorHandler.handleError(sender, "check if the specified player had an island", "SQL_ISLAND_GET", throwable);
                        return;
                    } else if (!hasIsland) {
                        Messages.PLAYER_HAS_NO_ISLAND.send(sender);
                        return;
                    }

                    GemsAPI.addGems(offlinePlayer.getUniqueId(), amount).whenComplete((ignored, exc) -> {
                        if (exc != null) {
                            ErrorHandler.handleError(sender, "give someone gems", "SQL_GEMS_MODIFY", exc);
                            return;
                        }

                        Messages.GEM_GIVE_SUCCESS.send(sender, "%amount%", amount, "%name%", offlinePlayer.getName());
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

                    faction.addGems(amount).whenComplete((ignored, ex) -> {
                        if (ex != null) {
                            ErrorHandler.handleError(sender, "give Gems to the specified Faction", "SQL_GEMS_MODIFY", ex);
                            return;
                        }

                        Messages.GEM_GIVE_SUCCESS.send(sender, "%amount%", amount, "%name%", playerFactionName);
                    });
            });
        } else {
            Messages.INCORRECT_USAGE.send(sender, "%usage%", getSyntax());
        }
    }

    public static List<String> permissions = List.of("skyfactions.gems.give");

    @Override
    public List<String> permission() {
        return permissions;
    }
}