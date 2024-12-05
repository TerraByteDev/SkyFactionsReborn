package net.skullian.skyfactions.common.command.faction.cmds;

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

import java.util.List;

@Command("faction")
public class FactionCreateCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "faction";
    }

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
            SkyUser player,
            @Argument(value = "name") String name
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        Messages.FACTION_CREATION_PROCESSING.send(player, locale);

        SkyApi.getInstance().getIslandAPI().getPlayerIsland(player.getUniqueId()).whenComplete((island, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your island", "SQL_ISLAND_GET", throwable);
                return;
            } else if (island == null) {
                Messages.FACTION_PLAYER_ISLAND_REQUIRED.send(player, locale);
                return;
            }

            SkyApi.getInstance().getFactionAPI().isInFaction(player.getUniqueId()).whenComplete((isInFaction, ex) -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                    return;
                }

                if (isInFaction) {
                    Messages.ALREADY_IN_FACTION.send(player, locale);
                } else {
                    SkyApi.getInstance().getFactionAPI().getFaction(name).whenComplete((faction, exc) -> {
                        if (exc != null) {
                            ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", exc);
                            return;
                        }

                        if (faction != null) {
                            Messages.FACTION_CREATION_NAME_DUPLICATE.send(player, locale);
                        } else {
                            if (SkyApi.getInstance().getFactionAPI().hasValidName(player, name)) {
                                int cost = Settings.FACTION_CREATION_COST.getInt();
                                if (cost > 0) {
                                    player.getRunes().whenComplete((runes, err) -> {
                                        if (err != null) {
                                            ErrorUtil.handleError(player, "get your runes balance", "SQL_RUNES_GET", err);
                                        } else if (runes < cost) {
                                            Messages.FACTION_INSUFFICIENT_FUNDS.send(player, locale, "creation_cost", cost);
                                        } else {
                                            player.removeRunes(cost);
                                            SkyApi.getInstance().getFactionAPI().createFaction(player, name);
                                        }
                                    });
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
