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
public class IslandUntrustCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "island";
    }

    @Override
    public String getName() {
        return "untrust";
    }

    @Override
    public String getDescription() {
        return "Untrust a player, so they can not longer visit";
    }

    @Override
    public String getSyntax() {
        return "/untrust <player name>";
    }

    @Suggestions("onlinePlayers")
    public List<String> suggestPlayers(CommandContext<SkyUser> context, CommandInput input) {
        return SkyApi.getInstance().getPlayerAPI().getOnlinePlayers().stream()
                .map(SkyUser::getName)
                .collect(Collectors.toList());
    }

    @Command("untrust <target>")
    @Permission(value = {"skyfactions.island.untrust", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "target", suggestions = "onlinePlayers") String playerName
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        
        SkyUser target = SkyApi.getInstance().getUserManager().getUser(playerName);

        SkyApi.getInstance().getPlayerAPI().isPlayerRegistered(target.getUniqueId()).whenComplete((isRegistered, err) -> {
            if (err != null) {
                ErrorUtil.handleError(player, "check if that player is registered", "SQL_PLAYER_GET", err);
                return;
            } else if (!isRegistered) {
                Messages.UNKNOWN_PLAYER.send(player, locale, "player", playerName);
                return;
            }

            SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((is, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                    return;
                } else if (is == null) {
                    Messages.NO_ISLAND.send(player, locale);
                    return;
                }

                SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().isPlayerTrusted(target.getUniqueId(), is.getId()).whenComplete((isTrusted, throwable) -> {
                    if (throwable != null) {
                        ErrorUtil.handleError(player, "check if a player is trusted", "SQL_TRUST_GET", throwable);
                        return;
                    }

                    if (isTrusted) {
                        SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().removePlayerTrust(target.getUniqueId(), is.getId()).whenComplete((ignored, exc) -> {
                            if (exc != null) {
                                ErrorUtil.handleError(player, "untrust a player", "SQL_TRUST_REMOVE", exc);
                                return;
                            }

                            Messages.UNTRUST_SUCCESS.send(player, locale, "player", target.getName());
                        });
                    } else {
                        Messages.UNTRUST_FAILURE.send(player, locale);
                    }
                });
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.untrust", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
