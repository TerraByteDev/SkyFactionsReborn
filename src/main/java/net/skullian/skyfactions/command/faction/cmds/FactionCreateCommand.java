package net.skullian.skyfactions.command.faction.cmds;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.ErrorUtil;
import org.bukkit.command.CommandSender;
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
            CommandSourceStack commandSourceStack,
            @Argument(value = "name") String name
    ) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        Messages.FACTION_CREATION_PROCESSING.send(player, PlayerHandler.getLocale(player.getUniqueId()));

        FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player, PlayerHandler.getLocale(player.getUniqueId()));
            } else {
                FactionAPI.getFaction(name).whenComplete((faction, exc) -> {
                    if (exc != null) {
                        ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", exc);
                        return;
                    }

                    if (faction != null) {
                        Messages.FACTION_CREATION_NAME_DUPLICATE.send(player, PlayerHandler.getLocale(player.getUniqueId()));
                    } else {
                        if (FactionAPI.hasValidName(player, name)) {
                            int cost = Settings.FACTION_CREATION_COST.getInt();
                            if (cost > 0) {
                                int runes = RunesAPI.getRunes(player.getUniqueId());

                                if (runes < cost) {
                                    Messages.FACTION_INSUFFICIENT_FUNDS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "creation_cost", cost);
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
    }

    public static List<String> permissions = List.of("skyfactions.faction.create", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
