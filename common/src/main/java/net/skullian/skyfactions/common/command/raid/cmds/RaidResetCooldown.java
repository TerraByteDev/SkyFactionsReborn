package net.skullian.skyfactions.common.command.raid.cmds;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.command.CommandTemplate;
import net.skullian.skyfactions.common.command.CommandsUtility;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("raid")
public class RaidResetCooldown extends CommandTemplate {
    @Override
    public String getParent() {
        return "raid";
    }

    @Override
    public String getName() {
        return "resetcooldown";
    }

    @Override
    public String getDescription() {
        return "Reset your raid cooldown.";
    }

    @Override
    public String getSyntax() {
        return "/raid resetcooldown";
    }

    @Command("resetcooldown")
    @Permission(value = {"skyfactions.raid.resetcooldown"}, mode = Permission.Mode.ANY_OF)
    public void perform(
            SkyUser player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SkyApi.getInstance().getCacheService().getEntry(player.getUniqueId()).setNewLastRaid(player.getUniqueId(), 0);
        player.sendMessage(TextUtility.color("<green>Successfully reset your raid cooldown.", SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()), player));
    }

    public static List<String> permissions = List.of("skyfactions.raid.resetcooldown");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
