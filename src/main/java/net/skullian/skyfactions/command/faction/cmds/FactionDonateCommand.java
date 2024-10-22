package net.skullian.skyfactions.command.faction.cmds;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.faction.FactionCommandHandler;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.util.ErrorHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command("faction")
public class FactionDonateCommand extends CommandTemplate {

    @Override
    public String getName() { return "donate"; }

    @Override
    public String getDescription() { return "Donate your gems the faction."; }

    @Override
    public String getSyntax() { return "/faction donate <gems>"; }

    @Command("donate <amount>")
    @Permission(value = { "skyfactions.faction.donate", "skyfactions.faction" }, mode = Permission.Mode.ANY_OF)
    public void perform(
            CommandSourceStack commandSourceStack,
            @Argument("amount") int amount
    ) {
        CommandSender sender = commandSourceStack.getSender();
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

           int gems = GemsAPI.getGems(player.getUniqueId());
           if (gems < amount) {
               Messages.INSUFFICIENT_GEMS_COUNT.send(player);
               return;
           }
           GemsAPI.subtractGems(player.getUniqueId(), amount);
           faction.addGems(amount);
           Messages.FACTION_GEMS_DONATION_SUCCESS.send(player, "%amount%", amount);
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.donate", "skyfactions.faction");

    @Override
    public List<String> permission() { return permissions; }
}
