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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;
import java.util.stream.Collectors;

@Command("island")
public class IslandUntrustCommand extends CommandTemplate {
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
    public List<String> suggestPlayers(CommandContext<CommandSender> context, CommandInput input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Command("untrust <player>")
    public void perform(
            CommandSender sender,
            @Argument(value = "playerName", suggestions = "onlinePlayers") String playerName
    ) {
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

        if (!target.hasPlayedBefore()) {
            Messages.UNKNOWN_PLAYER.send(player, "%player%", playerName);
            return;
        }

        IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((is, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (is == null) {
                Messages.NO_ISLAND.send(player);
                return;
            }

            SkyFactionsReborn.databaseHandler.isPlayerTrusted(target.getUniqueId(), is.getId()).whenComplete((isTrusted, throwable) -> {
                if (throwable != null) {
                    ErrorHandler.handleError(player, "check if a player is trusted", "SQL_TRUST_GET", throwable);
                    return;
                }

                if (isTrusted) {
                    SkyFactionsReborn.databaseHandler.removeTrust(target.getPlayer(), is.getId()).whenComplete((ignored, exc) -> {
                        if (exc != null) {
                            ErrorHandler.handleError(player, "untrust a player", "SQL_TRUST_REMOVE", exc);
                            return;
                        }

                        Messages.UNTRUST_SUCCESS.send(player, "%player%", target.getName());
                    });
                } else {
                    Messages.UNTRUST_FAILURE.send(player);
                }
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.untrust", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
