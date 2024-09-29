package net.skullian.torrent.skyfactions.api;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.types.Messages;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletionException;

public class GemsAPI {

    /**
     * Get a player's gem count.
     *
     * @param player Player you want to get gem count from.
     * @return {@link Integer}
     */
    public static int getGems(Player player) {
        try {
            return SkyFactionsReborn.db.getGems(player).join();
        } catch (CompletionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "get your gems count", "%debug%", "SQL_GEMS_GET");
        }

        return 0;
    }

    /**
     * Add gems to a player.
     *
     * @param player   Player to give gems to.
     * @param addition Amount of gems to add.
     */
    public static void addGems(Player player, int addition) {
        try {
            SkyFactionsReborn.db.addGems(player, addition).join();
        } catch (CompletionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "add gems", "%debug%", "SQL_GEMS_ADD");
        }
    }

    /**
     * Subtract gems from a player.
     *
     * @param player      Player to subtract gems from.
     * @param subtraction Amount of gems to remove.
     */
    public static void subtractGems(Player player, int subtraction) {
        try {
            SkyFactionsReborn.db.subtractGems(player, getGems(player), subtraction).join();
        } catch (CompletionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "decrease your gems count", "%debug%", "SQL_GEMS_SUBTRACT");
        }
    }
}
