package net.skullian.skyfactions.common.command.faction.cmds;

import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotGemsAPI;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionDonateCommand extends CommandTemplate {

    @Override
    public String getName() {
        return "donate";
    }

    @Override
    public String getDescription() {
        return "Donate your gems the faction.";
    }

    @Override
    public String getSyntax() {
        return "/faction donate <gems>";
    }

    @Command("donate <amount>")
    @Permission(value = {"skyfactions.faction.donate", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            Player player,
            @Argument("amount") int amount
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", throwable);
                return;
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return;
            }

            SpigotGemsAPI.getGems(player.getUniqueId()).whenComplete((gems, err) -> {
                if (err != null) {
                    ErrorUtil.handleError(player, "get your gems", "SQL_GEMS_GET", err);
                    return;
                } else if (gems < amount) {
                    Messages.INSUFFICIENT_GEMS_COUNT.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                    return;
                }

                SpigotGemsAPI.subtractGems(player.getUniqueId(), amount);
                faction.addGems(amount);
                Messages.FACTION_GEMS_DONATION_SUCCESS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "amount", amount);
            });
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.donate", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
