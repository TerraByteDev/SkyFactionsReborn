package net.skullian.torrent.skyfactions.command.raid.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.raid.RaidManager;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.gui.PlayerRaidConfirmationUI;
import org.bukkit.entity.Player;

import java.sql.SQLException;

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

        try {

            String cooldownLeft = RaidManager.getCooldownDuration(player);
            if (cooldownLeft != null) {
                Messages.RAID_ON_COOLDOWN.send(player, "%cooldown%", cooldownLeft);
            } else {
                PlayerRaidConfirmationUI.promptPlayer(player);
                SoundUtil.playSound(player, "ui.button.click", 1f, 1f);
            }

        } catch (SQLException error) {
            SkyFactionsReborn.db.handleError(error);
            Messages.ERROR.send(player, "%operation%", "start a raid");
        }
    }

    @Override
    public String permission() {
        return "skyfactions.raid.start";
    }
}
