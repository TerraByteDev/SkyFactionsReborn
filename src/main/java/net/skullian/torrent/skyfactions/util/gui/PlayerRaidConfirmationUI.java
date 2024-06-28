package net.skullian.torrent.skyfactions.util.gui;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.util.gui.items.*;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;
import java.util.List;

public class PlayerRaidConfirmationUI {

    public static void promptPlayer(Player player) {
        try {
            GUIData data = GUIAPI.getGUIData("start_raid");
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), data);


            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setGui(gui)
                    .build();

            window.open();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "start a raid", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, GUIData guiData) {
        try {
            List<ItemData> data = GUIAPI.getItemData(guiData.getITEMS(), "start_raid");
            for (ItemData itemData : data) {

                switch (itemData.getITEM_ID()) {

                    case "CANCEL":
                        builder.addIngredient(itemData.getCHARACTER(), new CreationCancelItem(itemData));
                        break;
                    case "CONFIRM":
                        builder.addIngredient(itemData.getCHARACTER(), new CreationConfirmationItem(itemData));
                        break;
                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new CreationPromptItem(itemData));
                        break;
                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData));
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
