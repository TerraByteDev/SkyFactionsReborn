package net.skullian.torrent.skyfactions.command.island.cmds;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.api.SkullAPI;
import net.skullian.torrent.skyfactions.command.CommandTemplate;
import net.skullian.torrent.skyfactions.api.IslandAPI;
import net.skullian.torrent.skyfactions.obelisk.ObeliskHandler;
import net.skullian.torrent.skyfactions.util.gui.RuneSubmitUI;
import net.skullian.torrent.skyfactions.util.gui.data.GUIData;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

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
        try {
            GUIData data = GUIAPI.getGUIData("runes_ui");
            new RuneSubmitUI(RuneSubmitUI.createStructure(player, "player", data), data).promptPlayer(player);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String permission() {
        return "";
    }
}
