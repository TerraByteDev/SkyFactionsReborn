package net.skullian.skyfactions.paper.command.island.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
    public List<String> suggestPlayers(CommandContext<CommandSourceStack> context, CommandInput input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Command("trust <target>")
    @Permission(value = {"skyfactions.island.trust", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player,
            @Argument(value = "target", suggestions = "onlinePlayers") String playerName
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

        SpigotPlayerAPI.isPlayerRegistered(target.getUniqueId()).whenComplete((isRegistered, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "check if that player is registered", "SQL_PLAYER_GET", throwable);
                return;
            } else if (!isRegistered) {
                Messages.UNKNOWN_PLAYER.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "player", playerName);
                return;
            }

            SpigotIslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                    return;
                } else if (island == null) {
                    Messages.NO_ISLAND.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                    return;
                }

                SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().isPlayerTrusted(target.getUniqueId(), island.getId()).whenComplete((isTrusted, someThrowable) -> {
                    if (someThrowable != null) {
                        ErrorUtil.handleError(player, "check if a player is trusted", "SQL_TRUST_GET", someThrowable);
                        return;
                    }

                    if (isTrusted) {
                        Messages.PLAYER_ALREADY_TRUSTED.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                    } else {
                        SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().trustPlayer(target.getUniqueId(), island.getId()).whenComplete((result, exc) -> {
                            if (exc != null) {
                                ErrorUtil.handleError(player, "trust a player", "SQL_TRUST_ADD", exc);
                                return;
                            }

                            Messages.TRUST_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "player", target.getName());
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