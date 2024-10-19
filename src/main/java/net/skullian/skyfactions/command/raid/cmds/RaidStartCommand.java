package net.skullian.skyfactions.command.raid.cmds;

import net.skullian.skyfactions.api.RaidAPI;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.PlayerRaidConfirmationUI;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.SoundUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

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
    public void perform(Player player, String[] args) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;

        RaidAPI.getCooldownDuration(player).whenComplete((cooldown, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(player, "start a raid", "SQL_RAID_COOLDOWN_GET", ex);
                return;
            }

            if (cooldown != null) {
                Messages.RAID_ON_COOLDOWN.send(player, "%cooldown%", cooldown);
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
