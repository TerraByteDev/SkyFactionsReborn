package net.skullian.torrent.skyfactions.util.gui;

import net.skullian.torrent.skyfactions.util.gui.items.CreationCancelItem;
import net.skullian.torrent.skyfactions.util.gui.items.CreationConfirmationItem;
import net.skullian.torrent.skyfactions.util.gui.items.CreationPromptItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class IslandCreationConfirmationUI {

    public static void promptPlayer(Player player) {
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . a . . . #",
                        "# . c . . . d . #",
                        "# # # # # # # # #")
                .addIngredient('#', new SimpleItem(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)))
                .addIngredient('a', new CreationPromptItem())
                .addIngredient('c', new CreationConfirmationItem())
                .addIngredient('d', new CreationCancelItem())
                .build();

        Window window = Window.single()
                .setViewer(player)
                .setTitle("Create Island")
                .setGui(gui)
                .build();

        window.open();
    }

}
