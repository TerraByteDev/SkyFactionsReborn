package net.skullian.skyfactions.common.command.runes.subcommands;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
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
    public String getParent() {
        return "runes";
    }

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
    public List<String> selectionSuggestion(CommandContext<SkyUser> context, CommandInput input) {
        return List.of("player", "faction");
    }

    @Suggestions("playerFactionName")
    public List<String> suggestPlayerOrFaction(CommandContext<SkyUser> context, CommandInput input) {
        if (input.input().startsWith("runes give player")) {
            return SkyApi.getInstance().getPlayerAPI().getOnlinePlayers().stream()
                    .map(SkyUser::getName)
                    .collect(Collectors.toList());
        } else if (input.input().startsWith("runes give faction")) {
            return new ArrayList<>(SkyApi.getInstance().getFactionAPI().getFactionCache().keySet());
        }

        return List.of();
    }

    @Command("give <type> <playerFactionName> <amount> ")
    @Permission(value = {"skyfactions.runes.give"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser sender,
            @Argument(value = "type", suggestions = "giveTypeSelection") String type,
            @Argument(value = "playerFactionName", suggestions = "playerFactionName") String playerFactionName,
            @Argument(value = "amount") int amount

    ) {
        if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, permission(), true)) return;
        String locale = sender.isConsole() ? SkyApi.getInstance().getPlayerAPI().getLocale(sender.getUniqueId()) : Messages.getDefaulLocale();

        if (type.equalsIgnoreCase("player")) {
            SkyUser offlinePlayer = SkyApi.getInstance().getUserManager().getUser(playerFactionName);

            SkyApi.getInstance().getPlayerAPI().isPlayerRegistered(offlinePlayer.getUniqueId()).whenComplete((isRegistered, throwable) -> {
                 if (throwable != null) {
                     ErrorUtil.handleError(sender, "check if that player is registered", "SQL_PLAYER_GET", throwable);
                     return;
                 } else if (!isRegistered) {
                     Messages.UNKNOWN_PLAYER.send(sender, locale, "player", playerFactionName);
                     return;
                 }

                SkyApi.getInstance().getIslandAPI().getPlayerIsland(offlinePlayer.getUniqueId()).whenComplete((island, ex) -> {
                    if (ex != null) {
                        ErrorUtil.handleError(sender, "check if the player had an island", "SQL_ISLAND_GET", ex);
                        return;
                    } else if (island == null) {
                        Messages.PLAYER_HAS_NO_ISLAND.send(sender, locale);
                        return;
                    }

                    offlinePlayer.addRunes(amount);
                    Messages.RUNES_GIVE_SUCCESS.send(sender, locale, "amount", amount, "name", offlinePlayer.getName());
                });
            });
        } else if (type.equalsIgnoreCase("faction")) {

            SkyApi.getInstance().getFactionAPI().getFaction(playerFactionName).whenComplete((faction, throwable) -> {
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
