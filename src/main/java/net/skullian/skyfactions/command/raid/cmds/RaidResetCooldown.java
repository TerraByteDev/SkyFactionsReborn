package net.skullian.skyfactions.command.raid.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("raid")
public class RaidResetCooldown extends CommandTemplate {
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
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SkyFactionsReborn.getDatabaseManager().getPlayerManager().updateLastRaid(player, 0).whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "reset your raid cooldown", "SQL_RAID_UPDATE", throwable);
                return;
            }

            player.sendMessage(TextUtility.color("&aSuccessfully reset your raid cooldown.", PlayerAPI.getLocale(player.getUniqueId()), player));
        });
    }

    public static List<String> permissions = List.of("skyfactions.raid.resetcooldown");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
