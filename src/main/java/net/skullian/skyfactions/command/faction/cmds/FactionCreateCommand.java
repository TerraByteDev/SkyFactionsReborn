package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.List;

@Command("create")
public class FactionCreateCommand extends CommandTemplate {

    FactionCommandHandler handler;

    public FactionCreateCommand(FactionCommandHandler handler) {
        this.handler = handler;
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
    public void perform(
            CommandSender sender,
            @Argument(value = "name") String name
    ) {
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;
        Messages.FACTION_CREATION_PROCESSING.send(player);

        FactionAPI.isInFaction(player).whenComplete((isInFaction, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            }

            if (isInFaction) {
                Messages.ALREADY_IN_FACTION.send(player);
            } else {
                FactionAPI.getFaction(name).whenComplete((faction, exc) -> {
                    if (exc != null) {
                        ErrorHandler.handleError(player, "get the Faction", "SQL_FACTION_GET", exc);
                        return;
                    }

                    if (faction != null) {
                        Messages.FACTION_CREATION_NAME_DUPLICATE.send(player);
                    } else {
                        if (FactionAPI.hasValidName(player, name)) {
                            int cost = Settings.FACTION_CREATION_COST.getInt();
                            if (cost > 0) {
                                RunesAPI.getRunes(player.getUniqueId()).whenComplete((runes, throwable) -> {
                                    if (throwable != null) {
                                        ErrorHandler.handleError(player, "get your Runes count", "SQL_RUNES_GET", throwable);
                                        return;
                                    }

                                    if (runes < cost) {
                                        Messages.FACTION_INSUFFICIENT_FUNDS.send(player, "%creation_cost%", cost);
                                    } else {
                                        RunesAPI.removeRunes(player.getUniqueId(), cost).whenComplete((ignored, exce) -> {
                                            if (exce == null) FactionAPI.createFaction(player, name);
                                            else exce.printStackTrace();
                                        });
                                    }
                                });
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
