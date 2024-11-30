package net.skullian.skyfactions.paper.command.runes.subcommands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.api.SpigotRunesAPI;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.CommandsUtility;
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
public class RunesResetCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Reset a player's or Faction's runes.";
    }

    @Override
    public String getSyntax() {
        return "/runes reset <type> <player/faction>";
    }

    @Suggestions("resetTypeSelection")
    public List<String> selectionSuggestion(CommandContext<CommandSourceStack> context, CommandInput input) {
        return List.of("player", "faction");
    }

    @Suggestions("playerFactionName")
    public List<String> suggestPlayerOrFaction(CommandContext<CommandSourceStack> context, CommandInput input) {
        if (input.input().startsWith("runes reset player")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (input.input().startsWith("runes reset faction")) {
            return new ArrayList<>(SpigotFactionAPI.factionNameCache.keySet());
        }

        return List.of();
    }

    @Command("reset <type> <playerFactionName>")
    @Permission(value = {"skyfactions.runes.reset"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSender sender,
            @Argument(value = "type", suggestions = "giveTypeSelection") String type,
            @Argument(value = "playerFactionName", suggestions = "playerFactionName") String playerFactionName
    ) {
        if ((sender instanceof Player) && !CommandsUtility.hasPerm((Player) sender, permission(), true)) return;
        String locale = sender instanceof Player ? ((Player) sender).locale().getLanguage() : Messages.getDefaulLocale();

        if (type.equalsIgnoreCase("player")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerFactionName);

            SpigotPlayerAPI.isPlayerRegistered(player.getUniqueId()).whenComplete((isRegistered, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(sender, "check if that player is registered", "SQL_PLAYER_GET", throwable);
                    return;
                } else if (!isRegistered) {
                    Messages.UNKNOWN_PLAYER.send(sender, locale, "player", playerFactionName);
                    return;
                }

                SpigotIslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, ex) -> {
                    if (ex != null) {
                        ErrorUtil.handleError(sender, "check if the player had an island", "SQL_ISLAND_GET", ex);
                        return;
                    } else if (!hasIsland) {
                        Messages.PLAYER_HAS_NO_ISLAND.send(sender, locale);
                        return;
                    }

                    SpigotRunesAPI.getRunes(player.getUniqueId()).whenComplete((runes, ex2) -> {
                        if (ex2 != null) {
                            ErrorUtil.handleError(sender, "get the player's runes", "SQL_RUNES_GET", ex2);
                            return;
                        }

                        SpigotRunesAPI.removeRunes(player.getUniqueId(), runes);
                    });

                    Messages.RUNES_RESET_SUCCESS.send(sender, locale, "name", player.getName());
                });
            });
        }
    }



    @Override
    public List<String> permission() {
        return List.of("skyfactions.runes.reset");
    }
}
