package net.skullian.torrent.skyfactions.gui;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.gui.data.GUIData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.torrent.skyfactions.gui.items.GeneralCancelItem;
import net.skullian.torrent.skyfactions.gui.items.GeneralPromptItem;
import net.skullian.torrent.skyfactions.gui.items.faction_leave.LeaveConfirmationItem;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class FactionLeaveConfirmationUI {

    public static void promptPlayer(Player player) {
        try {
            GUIData data = GUIAPI.getGUIData("confirmations/faction_leave");
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "leave your faction", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player) {
        try {
            List<ItemData> data = GUIAPI.getItemData("confirmations/create_island", player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralPromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;
                    case "CONFIRM":
                        builder.addIngredient(itemData.getCHARACTER(), new LeaveConfirmationItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;
                    case "CANCEL":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralCancelItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;
                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
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
