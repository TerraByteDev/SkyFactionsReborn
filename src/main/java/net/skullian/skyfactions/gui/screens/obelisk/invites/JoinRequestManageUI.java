package net.skullian.skyfactions.gui.screens.obelisk.invites;

import java.util.List;

import org.bukkit.entity.Player;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.FactionJoinRequestAcceptItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.FactionJoinRequestRejectItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public class JoinRequestManageUI {

    public static void promptPlayer(Player player, InviteData inviteData) {
        try {
            GUIData data = GUIAPI.getGUIData("obelisk/invites/join_request_manage", player);
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player, inviteData);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(data.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open the join request manage GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, InviteData inviteData) {
        try {
            List<ItemData> data = GUIAPI.getItemData("obelisk/invites/join_request_manage", player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "PROMPT", "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction", player));
                        break;

                    case "REJECT":
                        builder.addIngredient(itemData.getCHARACTER(), new FactionJoinRequestRejectItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), inviteData, player));
                        break;

                    case "ACCEPT":
                        builder.addIngredient(itemData.getCHARACTER(), new FactionJoinRequestAcceptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), inviteData, player));
                        break;
                }
            }

        } catch (IllegalArgumentException error) {
            error.printStackTrace();
        }

        return builder;
    }
}
