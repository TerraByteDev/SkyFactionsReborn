package net.skullian.torrent.skyfactions.command;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.ConfigTypes;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.Settings;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownHandler {

    private static Map<UUID, Long> cooldowns = new HashMap<>();

    // returns true if the player is on cooldown
    // if not it returns false and the command can continue.
    public static boolean manageCooldown(Player player) {
        if (player.hasPermission("skyfactions.cooldown.bypass")) return false;
        long cooldownDuration = Settings.COMMAND_COOLDOWN.getInt();

        if (cooldowns.containsKey(player.getUniqueId())) {
            long secondsLeft = ((cooldowns.get(player.getUniqueId()) / 1000) + (cooldownDuration / 1000)) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                Messages.COOLDOWN.send(player, "%cooldown%", String.valueOf(secondsLeft));
                return true;
            } else {
                cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                return false;
            }
        }
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        return false;
    }
}
