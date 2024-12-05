package net.skullian.skyfactions.common.command.raid;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.screens.confirmation.PlayerRaidConfirmationUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("raid")
public class RaidStartCommand extends CommandTemplate {

    @Override
    public String getParent() {
        return "raid";
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Start a raid.";
    }

    @Override
    public String getSyntax() {
        return "/raid start";
    }

    @Command("start")
    @Permission(value = {"skyfactions.raid.start", "skyfactions.raid"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SkyApi.getInstance().getRaidAPI().getCooldownDuration(player).whenComplete((cooldown, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "start a raid", "SQL_RAID_COOLDOWN_GET", ex);
                return;
            }

            if (cooldown != null) {
                Messages.RAID_ON_COOLDOWN.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), "cooldown", cooldown);
            } else {
                PlayerRaidConfirmationUI.promptPlayer(player);
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.raid.start", "skyfactions.raid");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
