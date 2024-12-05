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
public class RunesResetCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "runes";
    }

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
    public List<String> selectionSuggestion(CommandContext<SkyUser> context, CommandInput input) {
        return List.of("player", "faction");
    }

    @Suggestions("playerFactionName")
    public List<String> suggestPlayerOrFaction(CommandContext<SkyUser> context, CommandInput input) {
        if (input.input().startsWith("runes reset player")) {
            return SkyApi.getInstance().getPlayerAPI().getOnlinePlayers().stream()
                    .map(SkyUser::getName)
                    .collect(Collectors.toList());
        } else if (input.input().startsWith("runes reset faction")) {
            return new ArrayList<>(SkyApi.getInstance().getFactionAPI().getFactionCache().keySet());
        }

        return List.of();
    }

    @Command("reset <type> <playerFactionName>")
    @Permission(value = {"skyfactions.runes.reset"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser sender,
            @Argument(value = "type", suggestions = "giveTypeSelection") String type,
            @Argument(value = "playerFactionName", suggestions = "playerFactionName") String playerFactionName
    ) {
        if (!sender.isConsole() && !CommandsUtility.hasPerm(sender, permission(), true)) return;
        String locale = sender.isConsole() ? SkyApi.getInstance().getPlayerAPI().getLocale(sender.getUniqueId()) : Messages.getDefaulLocale();

        if (type.equalsIgnoreCase("player")) {
            SkyUser player = SkyApi.getInstance().getUserManager().getUser(playerFactionName);

            SkyApi.getInstance().getPlayerAPI().isPlayerRegistered(player.getUniqueId()).whenComplete((isRegistered, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(sender, "check if that player is registered", "SQL_PLAYER_GET", throwable);
                    return;
                } else if (!isRegistered) {
                    Messages.UNKNOWN_PLAYER.send(sender, locale, "player", playerFactionName);
                    return;
                }

                SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
                    if (ex != null) {
                        ErrorUtil.handleError(sender, "check if the player had an island", "SQL_ISLAND_GET", ex);
                        return;
                    } else if (island == null) {
                        Messages.PLAYER_HAS_NO_ISLAND.send(sender, locale);
                        return;
                    }

                    player.getRunes().whenComplete((runes, ex2) -> {
                        if (ex2 != null) {
                            ErrorUtil.handleError(sender, "get the player's runes", "SQL_RUNES_GET", ex2);
                            return;
                        }

                        player.removeRunes(runes);
                        Messages.RUNES_RESET_SUCCESS.send(sender, locale, "name", player.getName());
                    });
                });
            });
        }
    }



    @Override
    public List<String> permission() {
        return List.of("skyfactions.runes.reset");
    }
}
