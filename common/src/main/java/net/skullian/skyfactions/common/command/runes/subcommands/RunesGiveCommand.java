package net.skullian.skyfactions.common.command.runes.subcommands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.api.SpigotRunesAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.util.ErrorUtil;
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
        return "/runes give <type> <player/faction> <amount>";
    }

    @Suggestions("giveTypeSelection")
    public List<String> selectionSuggestion(CommandContext<CommandSourceStack> context, CommandInput input) {
        return List.of("player", "faction");
    }

    @Suggestions("playerFactionName")
    public List<String> suggestPlayerOrFaction(CommandContext<CommandSourceStack> context, CommandInput input) {
        if (input.input().startsWith("runes give player")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (input.input().startsWith("runes give faction")) {
            return new ArrayList<>(SpigotFactionAPI.factionNameCache.keySet());
        }

        return List.of();
    }

    @Command("give <type> <playerFactionName> <amount> ")
    @Permission(value = {"skyfactions.runes.give"}, mode = Permission.Mode.ANY_OF)
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

            SpigotPlayerAPI.isPlayerRegistered(offlinePlayer.getUniqueId()).whenComplete((isRegistered, throwable) -> {
                 if (throwable != null) {
                     ErrorUtil.handleError(sender, "check if that player is registered", "SQL_PLAYER_GET", throwable);
                     return;
                 } else if (!isRegistered) {
                     Messages.UNKNOWN_PLAYER.send(sender, locale, "player", playerFactionName);
                     return;
                 }

                SpigotIslandAPI.hasIsland(offlinePlayer.getUniqueId()).whenComplete((hasIsland, ex) -> {
                    if (ex != null) {
                        ErrorUtil.handleError(sender, "check if the player had an island", "SQL_ISLAND_GET", ex);
                        return;
                    } else if (!hasIsland) {
                        Messages.PLAYER_HAS_NO_ISLAND.send(sender, locale);
                        return;
                    }

                    SpigotRunesAPI.addRunes(offlinePlayer.getUniqueId(), amount);
                    Messages.RUNES_GIVE_SUCCESS.send(sender, locale, "amount", amount, "name", offlinePlayer.getName());
                });
            });
        } else if (type.equalsIgnoreCase("faction")) {

            SpigotFactionAPI.getFaction(playerFactionName).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(sender, "get the specified Faction", "SQL_FACTION_GET", throwable);
                    return;
                } else if (faction == null) {
                    Messages.FACTION_NOT_FOUND.send(sender, locale, "name", playerFactionName);
                    return;
                }

                faction.addRunes(amount);
                Messages.RUNES_GIVE_SUCCESS.send(sender, locale, "amount", amount, "name", faction.getName());
            });
        }
    }

    public static List<String> permissions = List.of("skyfactions.runes.give");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
