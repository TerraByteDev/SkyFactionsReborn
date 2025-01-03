package net.skullian.skyfactions.common.command.faction;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionRenameCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "faction";
    }

    @Override
    public String getName() {
        return "rename";
    }

    @Override
    public String getDescription() {
        return "Rename your Faction.";
    }

    @Override
    public String getSyntax() {
        return "/faction rename <name>";
    }

    @Command("rename <name>")
    @Permission(value = {"skyfactions.faction.rename", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "name") String name
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, locale);
                return;
            }

            if (faction.getLastRenamed() + Settings.FACTION_RENAME_COOLDOWN.getLong() > System.currentTimeMillis()) {
                long cooldownDuration = Settings.FACTION_RENAME_COOLDOWN.getLong() - (System.currentTimeMillis() - faction.getLastRenamed());
                Messages.FACTION_RENAME_ON_COOLDOWN.send(player, locale, "cooldown", DurationFormatUtils.formatDuration(cooldownDuration, "HH'h 'mm'm 'ss's'"));
            } else if (!faction.getOwner().getUniqueId().equals(player.getUniqueId())) { // todo make more customisable/b
                Messages.FACTION_RENAME_NO_PERMISSIONS.send(player, locale);
            } else {
                SkyApi.getInstance().getFactionAPI().getFaction(name).whenComplete((fetchedFaction, ex2) -> {
                    if (ex2 != null) {
                        ErrorUtil.handleError(player, "check if a Faction already exists with that name", "SQL_FACTION_GET", ex2);
                        return;
                    }

                    if (fetchedFaction != null) {
                        Messages.FACTION_CREATION_NAME_DUPLICATE.send(player, locale); // lazy
                    } else if (SkyApi.getInstance().getFactionAPI().hasValidName(player, name)) {
                        int cost = Settings.FACTION_RENAME_COST.getInt();
                        if (cost > 0 && (faction.getRunes() < cost)) {
                            Messages.FACTION_RENAME_INSUFFICIENT_FUNDS.send(player, locale, "rename_cost", cost);
                            return;
                        }

                        faction.updateName(name);
                    }
                });
            }
        });
    }

    @Override
    public List<String> permission() {
        return List.of("skyfactions.faction.rename", "skyfactions.faction");
    }
}
