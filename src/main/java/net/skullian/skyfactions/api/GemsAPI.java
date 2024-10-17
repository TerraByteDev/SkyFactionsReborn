package net.skullian.skyfactions.api;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GemsAPI {

    public static final Map<UUID, Integer> playerGems = new HashMap<>();

    /**
     * Get a player's gem count.
     *
     * @param playerUUID UUID of the player you want to get gem count from.
     * @return {@link Integer}
     */
    public static CompletableFuture<Integer> getGems(UUID playerUUID) {
        if (playerGems.containsKey(playerUUID)) return CompletableFuture.completedFuture(playerGems.get(playerUUID));
        return SkyFactionsReborn.databaseHandler.getGems(playerUUID).whenComplete((gems, ex) -> {
            if (ex != null) return;

            playerGems.put(playerUUID, gems);
        });
    }

    /**
     * Add gems to a player.
     *
     * @param playerUUID UUID of player to give gems to.
     * @param addition   Amount of gems to add.
     */
    public static CompletableFuture<Void> addGems(UUID playerUUID, int addition) {
        playerGems.replace(playerUUID, playerGems.get(playerUUID) + addition);
        return SkyFactionsReborn.databaseHandler.addGems(playerUUID, addition).exceptionally(ex -> {
            playerGems.replace(playerUUID, playerGems.get(playerUUID) - addition);
            return null;
        });
    }

    /**
     * Subtract gems from a player.
     *
     * @param playerUUID  UUID of player to subtract gems from.
     * @param subtraction Amount of gems to remove.
     */
    public static CompletableFuture<Void> subtractGems(UUID playerUUID, int subtraction) {
        playerGems.replace(playerUUID, playerGems.get(playerUUID) - subtraction);
        return SkyFactionsReborn.databaseHandler.subtractGems(playerUUID, subtraction).exceptionally((ex) -> {
            playerGems.replace(playerUUID, playerGems.get(playerUUID) + subtraction);
            return null;
        });
    }
}
