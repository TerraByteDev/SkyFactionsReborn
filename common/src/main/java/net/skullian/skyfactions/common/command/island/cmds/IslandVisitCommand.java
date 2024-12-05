package net.skullian.skyfactions.common.command.island.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotIslandAPI;
import net.skullian.skyfactions.paper.api.SpigotRaidAPI;
import net.skullian.skyfactions.paper.api.SpigotRegionAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.config.types.Settings;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@Command("island")
public class IslandVisitCommand extends CommandTemplate {

    IslandCommandHandler handler;

    public IslandVisitCommand(IslandCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getName() {
        return "visit";
    }

    @Override
    public String getDescription() {
        return "Visit another player's island if you are trusted.";
    }

    @Override
    public String getSyntax() {
        return "/island visit <player>";
    }

    @Suggestions("onlinePlayers")
    public List<String> suggestPlayers(CommandContext<CommandSourceStack> context, CommandInput input) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    @Command("visit <target>")
    @Permission(value = {"skyfactions.island.visit", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player,
            @Argument(value = "target", suggestions = "onlinePlayers") String playerName
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        Messages.VISIT_PROCESSING.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

        SpigotPlayerAPI.isPlayerRegistered(target.getUniqueId()).whenComplete((isRegistered, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "check if that player is registered", "SQL_PLAYER_GET", throwable);
                return;
            } else if (!isRegistered) {
                Messages.UNKNOWN_PLAYER.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "player", playerName);
                return;
            } else if (target.getUniqueId().equals(player.getUniqueId())) {
                IslandTeleportCommand template = (IslandTeleportCommand) this.handler.getSubCommands().get("teleport");
                template.perform(player);
                return;
            }

            SpigotIslandAPI.getPlayerIsland(target.getUniqueId()).whenComplete((is, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get that player's island", "SQL_ISLAND_GET", ex);
                    return;
                } else if (is == null) {
                    Messages.VISIT_NO_ISLAND.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                    return;
                } else if (SpigotRegionAPI.isLocationInRegion(player.getLocation(), "sfr_player_" + target.getUniqueId().toString())) {
                    Messages.VISIT_ALREADY_ON_ISLAND.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "player", target.getName());
                    return;
                }

                if ((SpigotRaidAPI.currentRaids.containsValue(player.getUniqueId()) || SpigotRaidAPI.processingRaid.containsValue(player.getUniqueId())) || (SpigotRaidAPI.currentRaids.containsValue(target.getUniqueId()) || SpigotRaidAPI.processingRaid.containsValue(target.getUniqueId()))) {
                    Messages.VISIT_IN_RAID.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                } else {
                    SkyFactionsReborn.getDatabaseManager().getPlayerIslandManager().isPlayerTrusted(player.getUniqueId(), is.getId()).whenComplete((isTrusted, err) -> {
                        if (err != null) {
                            ErrorUtil.handleError(player, "check if your are trusted", "SQL_TRUST_GET", err);
                            return;
                        }

                        World world = Bukkit.getWorld(Settings.ISLAND_PLAYER_WORLD.getString());
                        if (world == null) {
                            Messages.ERROR.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "operation", "visit a player", "debug", "WORLD_NOT_EXIST");
                        } else {
                            if (isTrusted) {
                                SpigotIslandAPI.modifyDefenceOperation(SpigotFactionAPI.DefenceOperation.DISABLE, player.getUniqueId());

                                SpigotRegionAPI.modifyWorldBorder(player, is.getCenter(world), is.getSize()); // shift the worldborder
                                SpigotRegionAPI.teleportPlayerToLocation(player, is.getCenter(world));

                                SpigotIslandAPI.onIslandLoad(target.getUniqueId());
                            } else {
                                Messages.PLAYER_NOT_TRUSTED.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                            }
                        }
                    });
                }
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.island.visit", "skyfactions.island");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
