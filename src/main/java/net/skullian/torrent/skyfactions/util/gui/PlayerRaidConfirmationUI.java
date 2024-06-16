package net.skullian.torrent.skyfactions.util.gui;

import net.skullian.torrent.skyfactions.util.gui.items.RaidCancelItem;
import net.skullian.torrent.skyfactions.util.gui.items.RaidConfirmationItem;
import net.skullian.torrent.skyfactions.util.gui.items.RaidPromptItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class PlayerRaidConfirmationUI {

    public static void promptPlayer(Player player) {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . a . . . #",
                        "# . c . . . d . #",
                        "# # # # # # # # #")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)))
                .addIngredient('a', new RaidPromptItem())
                .addIngredient('c', new RaidConfirmationItem())
                .addIngredient('d', new RaidCancelItem())
                .build();

        Window window = Window.single()
                .setViewer(player)
                .setTitle("Start Raid")
                .setGui(gui)
                .build();

        window.open();
    }
}
