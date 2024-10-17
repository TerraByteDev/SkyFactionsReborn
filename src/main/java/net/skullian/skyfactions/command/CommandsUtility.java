package net.skullian.skyfactions.command;

import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import org.bukkit.entity.Player;
import java.util.*;

public class CommandsUtility {

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

    public static boolean hasPerm(Player player, List<String> node, boolean sendDeny) {
        for (String perm : node) {
            if (player.hasPermission(perm)) return true;
        }

        if (sendDeny) {
            Messages.PERMISSION_DENY.send(player);
        }
        return false;
    }
}
