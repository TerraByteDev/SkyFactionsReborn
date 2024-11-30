package net.skullian.skyfactions.paper.command.faction.cmds;

import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.command.CommandTemplate;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import net.skullian.skyfactions.paper.util.ErrorUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Command("faction")
public class FactionDisbandCommand extends CommandTemplate {
    @Override
    public String getName() {
        return "disband";
    }

    @Override
    public String getDescription() {
        return "Disband your Faction. This will delete your faction island and remove all members.";
    }

    @Override
    public String getSyntax() {
        return "/faction disband (confirm)";
    }

    @Command("disband [confirm]")
    public void perform(
            Player player,
            @Argument(value = "confirm") @Nullable String confirm
    ) {
        String locale = SpigotPlayerAPI.getLocale(player.getUniqueId());

        SpigotFactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()));
                return;
            } else if (!faction.getOwner().getUniqueId().equals(player.getUniqueId())) {
                Messages.FACTION_DISBAND_OWNER_REQUIRED.send(player, locale);
                return;
            }

            if (confirm == null) {
                Messages.FACTION_DISBAND_COMMAND_CONFIRM.send(player, locale);
                SpigotFactionAPI.awaitingDeletion.add(faction.getName());
            } else if (confirm.equalsIgnoreCase("confirm")) {

                if (SpigotFactionAPI.awaitingDeletion.contains(faction.getName())) {
                    Messages.FACTION_DISBAND_DELETION_PROCESSING.send(player, locale);
                    SpigotFactionAPI.disbandFaction(player, faction);
                } else {
                    Messages.FACTION_DISBAND_COMMAND_BLOCK.send(player, locale);
                }
            } else {
                Messages.INCORRECT_USAGE.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "usage", getSyntax());
            }

        });
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.faction.disband", "skyfactions.faction");
    }
}
