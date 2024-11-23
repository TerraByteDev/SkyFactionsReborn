package net.skullian.skyfactions.command.gems.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
public class GemsGiveCommand extends CommandTemplate {

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
    @Permission(value = {"skyfactions.gems.give"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSender sender,
            @Argument(value = "type", suggestions = "giveTypeSelection") String type,
            @Argument(value = "playerFactionName", suggestions = "playerFactionName") String playerFactionName,
            @Argument(value = "amount") int amount
    ) {
        
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        String locale = sender instanceof Player ? ((Player) sender).locale().getLanguage() : Messages.getDefaulLocale();

        if (type.equalsIgnoreCase("player")) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerFactionName);

            PlayerAPI.isPlayerRegistered(offlinePlayer.getUniqueId()).whenComplete((isRegistered, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(sender, "check if that player is registered", "SQL_PLAYER_GET", ex);
                    return;
                } else if (!isRegistered) {
                    Messages.UNKNOWN_PLAYER.send(sender, locale, "player", playerFactionName);
                    return;
                }

                IslandAPI.hasIsland(offlinePlayer.getUniqueId()).whenComplete((hasIsland, throwable) -> {
                    if (throwable != null) {
                        ErrorUtil.handleError(sender, "check if the specified player had an island", "SQL_ISLAND_GET", throwable);
                        return;
                    } else if (!hasIsland) {
                        Messages.PLAYER_HAS_NO_ISLAND.send(sender, locale);
                        return;
                    }

                    GemsAPI.addGems(offlinePlayer.getUniqueId(), amount);
                    Messages.GEM_GIVE_SUCCESS.send(sender, locale, "amount", amount, "name", offlinePlayer.getName());
                });
            });
        } else if (type.equalsIgnoreCase("faction")) {
            FactionAPI.getFaction(playerFactionName).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(sender, "get the specified Faction", "SQL_FACTION_GET", throwable);
                    return;
                } else if (faction == null) {
                    Messages.FACTION_NOT_FOUND.send(sender, locale, "name", playerFactionName);
                    return;
                }

                faction.addGems(amount);
                Messages.GEM_GIVE_SUCCESS.send(sender, locale, "amount", amount, "name", playerFactionName);
            });
        } else {
            Messages.INCORRECT_USAGE.send(sender, locale, "usage", getSyntax());
        }
    }

    public static List<String> permissions = List.of("skyfactions.gems.give");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
