package net.skullian.torrent.skyfactions.command.raid.cmds;

import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.api.RaidAPI;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.gui.PlayerRaidConfirmationUI;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;

        String cooldownLeft = RaidAPI.getCooldownDuration(player);
        if (cooldownLeft != null) {
            if (cooldownLeft.equals("ERROR")) {
                Messages.ERROR.send(player, "%error%", "start a raid", "%debug%", "SQL_RAID_COOLDOWN_GET");
            } else {
                Messages.RAID_ON_COOLDOWN.send(player, "%cooldown%", cooldownLeft);
            }
        } else {
            PlayerRaidConfirmationUI.promptPlayer(player);
            SoundUtil.playSound(player, "ui.button.click", 1f, 1f);
        }
    }

    public static List<String> permissions = List.of("skyfactions.raid.start", "skyfactions.raid", "skyfactions.player");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
