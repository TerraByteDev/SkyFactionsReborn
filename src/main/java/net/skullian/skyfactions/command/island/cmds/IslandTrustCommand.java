package net.skullian.skyfactions.command.island.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
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

    @Command("trust <player>")
    @Permission(value = { "skyfactions.island.trust", "skyfactions.island" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack,
            @Argument(value = "player", suggestions = "onlinePlayers") String playerName
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

        if (!target.hasPlayedBefore()) {
            Messages.UNKNOWN_PLAYER.send(player, "%player%", playerName);
            return;
        }

        IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((island, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (island == null) {
                Messages.NO_ISLAND.send(player);
                return;
            }

            SkyFactionsReborn.databaseHandler.isPlayerTrusted(target.getUniqueId(), island.getId()).whenComplete((isTrusted, throwable) -> {
                if (throwable != null) {
                    ErrorHandler.handleError(player, "check if a player is trusted", "SQL_TRUST_GET", throwable);
                    return;
                }

                if (isTrusted) {
                    Messages.PLAYER_ALREADY_TRUSTED.send(player);
                } else {
                    SkyFactionsReborn.databaseHandler.trustPlayer(target.getUniqueId(), island.getId()).whenComplete((result, exc) -> {
                        if (exc != null) {
                            ErrorHandler.handleError(player, "trust a player", "SQL_TRUST_ADD", exc);
                            return;
                        }

                        Messages.TRUST_SUCCESS.send(player, "%player%", target.getName());
                    });
                }
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.trust", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
