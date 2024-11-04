package net.skullian.skyfactions.command.island.cmds;

import java.util.List;
import java.util.stream.Collectors;

import net.skullian.skyfactions.event.PlayerHandler;
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

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorUtil;

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
    public List<String> suggestPlayers(CommandContext<CommandSourceStack> context, CommandInput input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Command("untrust <player>")
    @Permission(value = { "skyfactions.island.untrust", "skyfactions.island" }, mode = Permission.Mode.ANY_OF)
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
            Messages.UNKNOWN_PLAYER.send(player, PlayerHandler.getLocale(player.getUniqueId()), "player", playerName);
            return;
        }

        IslandAPI.getPlayerIsland(player.getUniqueId()).whenComplete((is, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", ex);
                return;
            } else if (is == null) {
                Messages.NO_ISLAND.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                return;
            }

            SkyFactionsReborn.databaseManager.isPlayerTrusted(target.getUniqueId(), is.getId()).whenComplete((isTrusted, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "check if a player is trusted", "SQL_TRUST_GET", throwable);
                    return;
                }

                if (isTrusted) {
                    SkyFactionsReborn.databaseManager.removeTrust(target.getUniqueId(), is.getId()).whenComplete((ignored, exc) -> {
                        if (exc != null) {
                            ErrorUtil.handleError(player, "untrust a player", "SQL_TRUST_REMOVE", exc);
                            return;
                        }

                        Messages.UNTRUST_SUCCESS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "player", target.getName());
                    });
                } else {
                    Messages.UNTRUST_FAILURE.send(player, PlayerHandler.getLocale(player.getUniqueId()));
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
