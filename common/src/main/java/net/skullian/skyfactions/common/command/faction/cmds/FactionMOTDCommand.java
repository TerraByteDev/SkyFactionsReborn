package net.skullian.skyfactions.common.command.faction.cmds;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("faction")
public class FactionMOTDCommand extends CommandTemplate {
    @Override
    public String getParent() {
        return "faction";
    }

    @Override
    public String getName() {
        return "motd";
    }

    @Override
    public String getDescription() {
        return "Set your faction's MOTD (Message of the Day).";
    }

    @Override
    public String getSyntax() {
        return "/faction motd <motd>";
    }

    @Command("motd <motd>")
    @Permission(value = {"skyfactions.faction.motd", "skyfactions.faction"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player,
            @Argument(value = "motd") @Greedy String motd
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
            
        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
            } else if (faction == null) {
                Messages.NOT_IN_FACTION.send(player, locale);
                return;
            } else if (!Settings.FACTION_MODIFY_MOTD_PERMISSIONS.getList().contains(faction.getRankType(player.getUniqueId()).getRankValue())) {
                Messages.PERMISSION_DENY.send(player, locale);
                return;
            }

            Messages.MOTD_CHANGE_PROCESSING.send(player, locale);

            if (!TextUtility.containsBlockedPhrases(motd)) {
                faction.updateMOTD(motd, player);
                Messages.MOTD_CHANGE_SUCCESS.send(player, locale);
            } else Messages.BLACKLISTED_PHRASE.send(player, locale);
        });
    }

    public static List<String> permissions = List.of("skyfactions.faction.motd", "skyfactions.faction");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
