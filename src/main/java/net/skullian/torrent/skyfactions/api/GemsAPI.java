package net.skullian.torrent.skyfactions.api;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class GemsAPI {

    public static int getGems(Player player) {
        try {
            AtomicInteger gemCount = new AtomicInteger();

            SkyFactionsReborn.db.getGems(player).thenAccept(gemCount::set).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "get your gems count", "%debug%", "SQL_GEMS_GET");
                return null;
            }).get();

            return gemCount.get();
        } catch (InterruptedException | ExecutionException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "get your gems count", "%debug%", "SQL_GEMS_GET");
        }

        return 0;
    }

    public static void addGems(Player player, int addition) {
        try {
            SkyFactionsReborn.db.addGems(player, getGems(player), addition).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "add gems", "%debug%", "SQL_GEMS_ADD");
                return null;
            }).get();
        } catch (ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "add gems", "%debug%", "SQL_GEMS_ADD");
        }
    }

    public static void subtractGems(Player player, int subtraction) {
        try {
            SkyFactionsReborn.db.subtractGems(player, getGems(player), subtraction).exceptionally(ex -> {
                ex.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "decrease your gems count", "%debug%", "SQL_GEMS_SUBTRACT");
                return null;
            }).get();
        } catch (ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "decrease your gems count", "%debug%", "SQL_GEMS_SUBTRACT");
        }
    }
}
