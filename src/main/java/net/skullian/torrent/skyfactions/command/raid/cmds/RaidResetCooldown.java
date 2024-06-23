package net.skullian.torrent.skyfactions.command.raid.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.command.CooldownHandler;
import net.skullian.torrent.skyfactions.command.PermissionsHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        return "/raid resetcd";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!PermissionsHandler.hasPerm(player, permission(), true)) return;
        if (CooldownHandler.manageCooldown(player)) return;
        
        SkyFactionsReborn.db.updateLastRaid(player, 0).thenAccept(result -> {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reset your raid cooldown."));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public String permission() {
        return "skyfactions.raid.resetcooldown";
    }
}