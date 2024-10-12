package net.skullian.skyfactions.api;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GemsAPI {

    /**
     * Get a player's gem count.
     *
     * @param player Player you want to get gem count from.
     * @return {@link Integer}
     */
    public static CompletableFuture<Integer> getGems(Player player) {
        try {
            return SkyFactionsReborn.databaseHandler.getGems(player.getUniqueId());
        } catch (CompletionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "get your gems count", "%debug%", "SQL_GEMS_GET");
        }

        return CompletableFuture.completedFuture(0);
    }

    /**
     * Add gems to a player.
     *
     * @param playerUUID UUID of player to give gems to.
     * @param addition   Amount of gems to add.
     */
    public static CompletableFuture<Void> addGems(UUID playerUUID, int addition) {
        return SkyFactionsReborn.databaseHandler.addGems(playerUUID, addition);
    }

    /**
     * Subtract gems from a player.
     *
     * @param playerUUID  UUID of player to subtract gems from.
     * @param subtraction Amount of gems to remove.
     */
    public static CompletableFuture<Void> subtractGems(UUID playerUUID, int subtraction) {
        return SkyFactionsReborn.databaseHandler.subtractGems(playerUUID, subtraction);
    }
}
