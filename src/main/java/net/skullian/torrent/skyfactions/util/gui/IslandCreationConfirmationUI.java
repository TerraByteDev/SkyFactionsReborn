package net.skullian.torrent.skyfactions.util.gui;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.util.gui.data.GUIData;
import net.skullian.torrent.skyfactions.util.gui.data.ItemData;
import net.skullian.torrent.skyfactions.util.gui.items.island_creation.CreationCancelItem;
import net.skullian.torrent.skyfactions.util.gui.items.island_creation.CreationConfirmationItem;
import net.skullian.torrent.skyfactions.util.gui.items.island_creation.CreationPromptItem;
import net.skullian.torrent.skyfactions.util.gui.items.GeneralBorderItem;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;
import java.util.List;

public class IslandCreationConfirmationUI implements Listener {

    public static void promptPlayer(Player player) {
        try {
            GUIData data = GUIAPI.getGUIData("confirmations/create_island");
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setGui(gui)
                    .build();

            window.open();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "create your island", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player) {
        try {
            List<ItemData> data = GUIAPI.getItemData("confirmations/create_island", player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "CANCEL":
                        builder.addIngredient(itemData.getCHARACTER(), new CreationCancelItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;
                    case "CONFIRM":
                        builder.addIngredient(itemData.getCHARACTER(), new CreationConfirmationItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;
                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new CreationPromptItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;
                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;
                }
            }

            return builder;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return builder;
    }

}