package net.skullian.torrent.skyfactions.gui.obelisk;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.gui.data.GUIData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.ObeliskHeadItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.ObeliskInvitesItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.ObeliskPlayerNotificationsItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.ObeliskRuneItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.defence.ObeliskDefencePurchaseItem;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class PlayerObeliskUI {

    public static void promptPlayer(Player player) {
        try {
            GUIData data = GUIAPI.getGUIData("obelisk/player_obelisk");
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
            Messages.ERROR.send(player, "%operation%", "open your obelisk", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player) {
        try {
            List<ItemData> data = GUIAPI.getItemData("obelisk/player_obelisk", player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "HEAD":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskHeadItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;

                    case "DEFENCES":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskDefencePurchaseItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", null));
                        break;

                    case "RUNES_CONVERSION":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskRuneItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player));
                        break;

                    case "INVITES":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskInvitesItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", null));
                        break;

                    case "NOTIFICATIONS":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskPlayerNotificationsItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
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
