package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.SkullAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.api.IslandAPI;
import net.skullian.torrent.skyfactions.obelisk.ObeliskHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        System.out.println("triggering");
        ObeliskHandler.spawnPlayerObelisk(player, SkyFactionsReborn.db.getPlayerIsland(player).join());
    }

    @Override
    public String permission() {
        return "";
    }
}
