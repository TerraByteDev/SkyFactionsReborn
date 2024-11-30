package net.skullian.skyfactions.common.gui;

import lombok.RequiredArgsConstructor;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CooldownManager {

    public static final CooldownManager ITEMS = new CooldownManager(Settings.ITEM_COOLDOWN.getInt());
    public static final CooldownManager COMMANDS = new CooldownManager(Settings.COMMAND_COOLDOWN.getInt());

    private final Map<UUID, Instant> cooldowns = new ConcurrentHashMap<>();
    private final int cooldownMillis;

    /**
     * Checks if the player is on cooldown and handles it accordingly.
     *
     * @param player The player to check and manage cooldown for.
     * @return True if the player is on cooldown, false otherwise.
     */
    public boolean manage(@NotNull SkyUser player) {
        if (player.hasPermission("skyfactions.cooldown.bypass")) return false;

        UUID playerId = player.getUniqueId();
        Instant now = Instant.now();
        Instant cooldownEnd = cooldowns.get(playerId);

        if (cooldownEnd != null && now.isBefore(cooldownEnd)) {
            long secondsLeft = Duration.between(now, cooldownEnd).getSeconds();
            Messages.COOLDOWN.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(playerId), "cooldown", String.valueOf(secondsLeft));
            return true;
        }

        cooldowns.put(player.getUniqueId(), now.plusMillis(cooldownMillis));
        return false;
    }

    /**
     * Removes the player's cooldown entry from the map.
     *
     * @param player The player whose cooldown is to be removed. Must not be null.
     */
    public void remove(@NotNull SkyUser player) {
        cooldowns.remove(player.getUniqueId());
    }
}
