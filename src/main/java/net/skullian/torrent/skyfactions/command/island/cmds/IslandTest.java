package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.api.IslandAPI;
import org.bukkit.entity.Player;

public class IslandTest extends CommandTemplate {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public String getSyntax() {
        return "/island test";
    }

    @Override
    public void perform(Player player, String[] args) {
        SkyFactionsReborn.db.getPlayerIsland(player).thenAccept(island -> {
            IslandAPI.removePlayerIsland(player);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public String permission() {
        return "";
    }
}
