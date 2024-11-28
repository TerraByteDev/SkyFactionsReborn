package net.skullian.skyfactions.core.command.raid.cmds;

import net.skullian.skyfactions.core.api.SpigotRaidAPI;
import net.skullian.skyfactions.core.command.CommandTemplate;
import net.skullian.skyfactions.core.command.CommandsUtility;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
import net.skullian.skyfactions.common.gui.screens.confirmation.PlayerRaidConfirmationUI;
import net.skullian.skyfactions.core.util.ErrorUtil;
import net.skullian.skyfactions.core.util.SoundUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

@Command("raid")
public class RaidStartCommand extends CommandTemplate {
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
            Player player
    ) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;

        SpigotRaidAPI.getCooldownDuration(player).whenComplete((cooldown, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "start a raid", "SQL_RAID_COOLDOWN_GET", ex);
                return;
            }

            if (cooldown != null) {
                Messages.RAID_ON_COOLDOWN.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "cooldown", cooldown);
            } else {
                PlayerRaidConfirmationUI.promptPlayer(player);
                SoundUtil.playSound(player, "ui.button.click", 1f, 1f);
            }
        });
    }

    public static List<String> permissions = List.of("skyfactions.raid.start", "skyfactions.raid");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
