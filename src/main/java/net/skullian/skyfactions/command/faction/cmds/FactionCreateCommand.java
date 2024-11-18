package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.IslandAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionCreateCommand extends CommandTemplate {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a new faction.";
    }

    @Override
    public String getSyntax() {
        return "/faction create <name>";
    }

    @Command("create <name>")
    @Permission(value = {"skyfactions.faction.create", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player,
            @Argument(value = "name") String name
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = PlayerHandler.getLocale(player.getUniqueId());

        Messages.FACTION_CREATION_PROCESSING.send(player, locale);

        IslandAPI.hasIsland(player.getUniqueId()).whenComplete((hasIsland, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", throwable);
                return;
            } else if (!hasIsland) {
                Messages.FACTION_PLAYER_ISLAND_REQUIRED.send(player, locale);
                return;
            }

            FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (isInFaction) {
                    Messages.ALREADY_IN_FACTION.send(player, locale);
                } else {
                    FactionAPI.getFaction(name).whenComplete((faction, exc) -> {
                        if (exc != null) {
                            ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", exc);
                            return;
                        }

                        if (faction != null) {
                            Messages.FACTION_CREATION_NAME_DUPLICATE.send(player, locale);
                        } else {
                            if (FactionAPI.hasValidName(player, name)) {
                                int cost = Settings.FACTION_CREATION_COST.getInt();
                                if (cost > 0) {
                                    int runes = RunesAPI.getRunes(player.getUniqueId());

                                    if (runes < cost) {
                                        Messages.FACTION_INSUFFICIENT_FUNDS.send(player, locale, "creation_cost", cost);
                                    } else {
                                        RunesAPI.removeRunes(player.getUniqueId(), cost);
                                        FactionAPI.createFaction(player, name);
                                    }
                                }
                            }
                        }
                    });
                }
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.create", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
