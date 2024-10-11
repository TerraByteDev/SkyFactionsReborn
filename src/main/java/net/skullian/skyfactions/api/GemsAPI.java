package net.skullian.skyfactions.api;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

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
            return SkyFactionsReborn.db.getGems(player);
        } catch (CompletionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "get your gems count", "%debug%", "SQL_GEMS_GET");
        }

        return CompletableFuture.completedFuture(0);
    }

    /**
     * Add gems to a player.
     *
     * @param player   Player to give gems to.
     * @param addition Amount of gems to add.
     */
    public static CompletableFuture<Void> addGems(Player player, int addition) {
        return SkyFactionsReborn.db.addGems(player, addition).exceptionally(ex -> {
            ex.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "add gems", "%debug%", "SQL_GEMS_ADD");
            return null;
        });
    }

    /**
     * Subtract gems from a player.
     *
     * @param player      Player to subtract gems from.
     * @param subtraction Amount of gems to remove.
     */
    public static CompletableFuture<Void> subtractGems(Player player, int subtraction) {
        try {
            return SkyFactionsReborn.db.subtractGems(player, subtraction);
        } catch (CompletionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "decrease your gems count", "%debug%", "SQL_GEMS_SUBTRACT");

            return null;
        }
    }
}
