package net.skullian.skyfactions.command.raid.cmds;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.command.CommandTemplate;
import net.skullian.skyfactions.command.CommandsUtility;
import net.skullian.skyfactions.command.CommandsUtility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

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

    @Override
    public void perform(Player player, String[] args) {
        if (!CommandsUtility.hasPerm(player, permission(), true)) return;
        if (CommandsUtility.manageCooldown(player)) return;
        
        SkyFactionsReborn.databaseHandler.updateLastRaid(player, 0).thenAccept(result -> {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully reset your raid cooldown."));
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public static List<String> permissions = List.of("skyfactions.raid.resetcooldown");

    @Override
    public List<String> permission() {
        return permissions;
    }
}
