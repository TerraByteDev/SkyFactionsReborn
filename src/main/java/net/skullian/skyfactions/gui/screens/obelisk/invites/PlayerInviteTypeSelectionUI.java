package net.skullian.skyfactions.gui.screens.obelisk.invites;

import java.util.List;

import net.skullian.skyfactions.config.types.GUIEnums;
import org.bukkit.entity.Player;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.JoinRequestsTypeItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.PlayerFactionInvitesTypeItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public class PlayerInviteTypeSelectionUI {

    public static void promptPlayer(Player player) {
        try {
            GUIData data = GUIAPI.getGUIData(GUIEnums.OBELISK_PLAYER_INVITE_TYPE_SELECTION_GUI.getInternalPath(), player);
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(data.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open the invite selection GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player) {
        try {
            List<ItemData> data = GUIAPI.getItemData(GUIEnums.OBELISK_PLAYER_INVITE_TYPE_SELECTION_GUI.getInternalPath(), player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "PROMPT", "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;

                    case "OUTGOING_JOIN_REQUEST":
                        builder.addIngredient(itemData.getCHARACTER(), new JoinRequestsTypeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player));
                        break;

                    case "INCOMING_INVITES":
                        builder.addIngredient(itemData.getCHARACTER(), new PlayerFactionInvitesTypeItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player));
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
