package net.skullian.torrent.skyfactions.gui;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.gui.data.GUIData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.items.*;
import net.skullian.torrent.skyfactions.gui.items.raid_start.RaidCancelItem;
import net.skullian.torrent.skyfactions.gui.items.raid_start.RaidConfirmationItem;
import net.skullian.torrent.skyfactions.util.SoundUtil;
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
            GUIData data = GUIAPI.getGUIData("confirmations/start_raid");
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player);


            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "start a raid", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player) {
        try {
            List<ItemData> data = GUIAPI.getItemData("confirmations/start_raid", player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "CANCEL":
                        builder.addIngredient(itemData.getCHARACTER(), new RaidCancelItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;
                    case "CONFIRM":
                        builder.addIngredient(itemData.getCHARACTER(), new RaidConfirmationItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;
                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralPromptItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;
                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;
                }
            }

            return builder;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return builder;
    }
}
