package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FactionDonateCommand extends CommandTemplate {

    @Override
    public String getName() { return "donate"; }

    @Override
    public String getDescription() { return "Donate your gems the faction."; }

    @Override
    public String getSyntax() { return "/faction donate <gems>"; }

    @Override
    public void perform(Player player, String[] args) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        Integer requestedDonation = Integer.parseInt(args[1]);

        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, throwable) -> {
           if (throwable != null) {
               ErrorHandler.handleError(player, "get your Faction", "SQL_FACTION_GET", throwable);
               return;
           } else if (faction == null) {
               Messages.NOT_IN_FACTION.send(player);
               return;
           }

            GemsAPI.getGems(player.getUniqueId()).whenComplete((gems, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "get your Gem count", "SQL_GEMS_GET", ex);
                    return;
                } else if (gems < requestedDonation) {
                    Messages.INSUFFICIENT_GEMS_COUNT.send(player);
                    return;
                }

                CompletableFuture.allOf(
                        GemsAPI.subtractGems(player.getUniqueId(), requestedDonation),
                        faction.addGems(requestedDonation)
                        ).whenComplete((ignored, exc) -> {

                            if (exc != null) {
                                ErrorHandler.handleError(player, "donate gems to your Faction", "SQL_GEMS_MODIFY", exc);
                                return;
                            }
                            Messages.FACTION_GEMS_DONATION_SUCCESS.send(player, "%amount%", requestedDonation);
                });
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.donate", "skyfactions.faction");

    @Override
    public List<String> permission() { return permissions; }
}
