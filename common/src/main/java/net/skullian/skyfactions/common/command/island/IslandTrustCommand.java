package net.skullian.skyfactions.common.command.island;

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

import java.util.List;
import java.util.stream.Collectors;

@Command("island")
public class IslandTrustCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "island";
    }

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

    @Suggestions("onlinePlayers")
    public List<String> suggestPlayers(CommandContext<SkyUser> context, CommandInput input) {
        return SkyApi.getInstance().getPlayerAPI().getOnlinePlayers().stream()
                .map(SkyUser::getName)
                .collect(Collectors.toList());
    }

    @Command("trust <target>")
    @Permission(value = {"skyfactions.island.trust", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "target", suggestions = "onlinePlayers") String playerName
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        
        SkyUser target = SkyApi.getInstance().getUserManager().getUser(playerName);

        SkyApi.getInstance().getPlayerAPI().isPlayerRegistered(target.getUniqueId()).whenComplete((isRegistered, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "check if that player is registered", "SQL_PLAYER_GET", throwable);
                return;
            } else if (!isRegistered) {
                Messages.UNKNOWN_PLAYER.send(player, locale, "player", playerName);
                return;
            }

            SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                    return;
                } else if (island == null) {
                    Messages.NO_ISLAND.send(player, locale);
                    return;
                }

                SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().isPlayerTrusted(target.getUniqueId(), island.getId()).whenComplete((isTrusted, someThrowable) -> {
                    if (someThrowable != null) {
                        ErrorUtil.handleError(player, "check if a player is trusted", "SQL_TRUST_GET", someThrowable);
                        return;
                    }

                    if (isTrusted) {
                        Messages.PLAYER_ALREADY_TRUSTED.send(player, locale);
                    } else {
                        SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().trustPlayer(target.getUniqueId(), island.getId()).whenComplete((result, exc) -> {
                            if (exc != null) {
                                ErrorUtil.handleError(player, "trust a player", "SQL_TRUST_ADD", exc);
                                return;
                            }

                            Messages.TRUST_SUCCESS.send(player, locale, "player", target.getName());
                        });
                    }
                });
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.trust", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
