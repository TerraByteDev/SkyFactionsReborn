package net.skullian.skyfactions.common.command.faction.cmds;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Command("faction")
public class FactionDisbandCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "faction";
    }

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
            SkyUser player,
            @Argument(value = "confirm") @Nullable String confirm
    ) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, locale);
                return;
            } else if (!faction.getOwner().getUniqueId().equals(player.getUniqueId())) {
                Messages.FACTION_DISBAND_OWNER_REQUIRED.send(player, locale);
                return;
            }

            if (confirm == null) {
                Messages.FACTION_DISBAND_COMMAND_CONFIRM.send(player, locale);
                SkyApi.getInstance().getFactionAPI().getAwaitingDeletion().add(faction.getName());
            } else if (confirm.equalsIgnoreCase("confirm")) {

                if (SkyApi.getInstance().getFactionAPI().getAwaitingDeletion().contains(faction.getName())) {
                    Messages.FACTION_DISBAND_DELETION_PROCESSING.send(player, locale);
                    SkyApi.getInstance().getFactionAPI().disbandFaction(player, faction);
                } else {
                    Messages.FACTION_DISBAND_COMMAND_BLOCK.send(player, locale);
                }
            } else {
                Messages.INCORRECT_USAGE.send(player, locale, "usage", getSyntax());
            }

        });
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.faction.disband", "skyfactions.faction");
    }
}
