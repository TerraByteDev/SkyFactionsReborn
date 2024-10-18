package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command("faction")
public class FactionDonateCommand extends CommandTemplate {

    FactionCommandHandler handler;

    public FactionDonateCommand(FactionCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getName() { return "donate"; }

    @Override
    public String getDescription() { return "Donate your gems the faction."; }

    @Override
    public String getSyntax() { return "/faction donate <gems>"; }

    @Command("donate <amount>")
    public void perform(
            CommandSender sender,
            @Argument("amount") int amount
    ) {
        if (!(sender instanceof Player player)) return;
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

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
                } else if (gems < amount) {
                    Messages.INSUFFICIENT_GEMS_COUNT.send(player);
                    return;
                }

                CompletableFuture.allOf(
                        GemsAPI.subtractGems(player.getUniqueId(), amount),
                        faction.addGems(amount)
                        ).whenComplete((ignored, exc) -> {

                            if (exc != null) {
                                ErrorHandler.handleError(player, "donate gems to your Faction", "SQL_GEMS_MODIFY", exc);
                                return;
                            }
                            Messages.FACTION_GEMS_DONATION_SUCCESS.send(player, "%amount%", amount);
                });
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.donate", "skyfactions.faction");

    @Override
    public List<String> permission() { return permissions; }
}
