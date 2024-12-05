package net.skullian.skyfactions.common.command.island.cmds;

import net.skullian.skyfactions.common.api.FactionAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
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

    @Override
    public String getParent() {
        return "island";
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
    public List<String> suggestPlayers(CommandContext<SkyUser> context, CommandInput input) {
        return SkyApi.getInstance().getPlayerAPI().getOnlinePlayers().stream()
                .map(SkyUser::getName)
                .collect(Collectors.toList());
    }

    @Command("visit <target>")
    @Permission(value = {"skyfactions.island.visit", "skyfactions.island"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "target", suggestions = "onlinePlayers") String playerName
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        Messages.VISIT_PROCESSING.send(player, locale);
        SkyUser target = SkyApi.getInstance().getUserManager().getUser(playerName);

        SkyApi.getInstance().getPlayerAPI().isPlayerRegistered(target.getUniqueId()).whenComplete((isRegistered, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "check if that player is registered", "SQL_PLAYER_GET", throwable);
                return;
            } else if (!isRegistered) {
                Messages.UNKNOWN_PLAYER.send(player, locale, "player", playerName);
                return;
            } else if (target.getUniqueId().equals(player.getUniqueId())) {
                IslandTeleportCommand template = (IslandTeleportCommand) SkyApi.getInstance().getCommandHandler().getSubCommands(getParent()).get("teleport");
                template.perform(player);
                return;
            }

            SkyApi.getInstance().getIslandAPI().getPlayerIsland(target.getUniqueId()).whenComplete((is, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get that player's island", "SQL_ISLAND_GET", ex);
                    return;
                } else if (is == null) {
                    Messages.VISIT_NO_ISLAND.send(player, locale);
                    return;
                } else if (SkyApi.getInstance().getRegionAPI().isLocationInRegion(player.getLocation(), "sfr_player_" + target.getUniqueId().toString())) {
                    Messages.VISIT_ALREADY_ON_ISLAND.send(player, locale, "player", target.getName());
                    return;
                }

                // todo - fix this shit
                if ((SkyApi.getInstance().getRaidAPI().currentRaids.containsValue(player.getUniqueId()) || SkyApi.getInstance().getRaidAPI().processingRaid.containsValue(player.getUniqueId())) || (SkyApi.getInstance().getRaidAPI().currentRaids.containsValue(target.getUniqueId()) || SkyApi.getInstance().getRaidAPI().processingRaid.containsValue(target.getUniqueId()))) {
                    Messages.VISIT_IN_RAID.send(player, locale);
                } else {
                    SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().isPlayerTrusted(player.getUniqueId(), is.getId()).whenComplete((isTrusted, err) -> {
                        if (err != null) {
                            ErrorUtil.handleError(player, "check if your are trusted", "SQL_TRUST_GET", err);
                            return;
                        }

                        String world = Settings.ISLAND_PLAYER_WORLD.getString();
                        if (SkyApi.getInstance().getRegionAPI().worldExists(world)) {
                            Messages.ERROR.send(player, locale, "operation", "visit a player", "debug", "WORLD_NOT_EXIST");
                        } else {
                            if (isTrusted) {
                                if (SkyApi.getInstance().getRegionAPI().isLocationInRegion(player.getLocation(), "sfr_player_" + player.getUniqueId().toString())) SkyApi.getInstance().getIslandAPI().modifyDefenceOperation(FactionAPI.DefenceOperation.DISABLE, player);

                                SkyApi.getInstance().getRegionAPI().modifyWorldBorder(player, is.getCenter(world), is.getSize()); // shift the worldborder
                                player.teleport(is.getCenter(world));

                                SkyApi.getInstance().getIslandAPI().onIslandLoad(target);
                            } else {
                                Messages.PLAYER_NOT_TRUSTED.send(player, locale);
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
