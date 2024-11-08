package net.skullian.skyfactions.util;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.event.PlayerHandler;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.services.type.ConsumerService;
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
    public boolean manage(@NotNull Player player) {
        if (player.hasPermission("skyfactions.cooldown.bypass")) return false;

        UUID playerId = player.getUniqueId();
        Instant now = Instant.now();
        Instant cooldownEnd = cooldowns.get(playerId);

        if (cooldownEnd != null && now.isBefore(cooldownEnd)) {
            long secondsLeft = Duration.between(now, cooldownEnd).getSeconds();
            Messages.COOLDOWN.send(player, PlayerHandler.getLocale(playerId), "cooldown", String.valueOf(secondsLeft));
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
    public void remove(@NotNull Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    /*
        Cooldown Post processor for using in commands
     */
    public static final class CooldownPostprocessor<C> implements CommandPostprocessor<C> {
        @Override
        public void accept(final @NonNull CommandPostprocessingContext<C> context) {
            if (context.commandContext().sender() instanceof CommandSourceStack css) {
                if (css.getSender() instanceof Player player) {
                    if (CooldownManager.COMMANDS.manage(player)) {
                        ConsumerService.interrupt();
                    }
                }
            }
        }
    }
}
