package net.skullian.torrent.skyfactions.gui.obelisk.invites;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.faction.JoinRequestData;
import net.skullian.torrent.skyfactions.gui.data.GUIData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestConfirmItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestDenyItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestRevoke;
import net.skullian.torrent.skyfactions.gui.items.obelisk.invites.PlayerJoinRequestPromptItem;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class PlayerOutgoingRequestManageUI {

    public static void promptPlayer(Player player, JoinRequestData joinRequest) {
        try {
            GUIData data = GUIAPI.getGUIData("obelisk/invites/player_join_request");
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player, joinRequest);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "manage your outgoing join request", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, JoinRequestData joinRequest) {

        List<ItemData> data = GUIAPI.getItemData("obelisk/invites/player_join_request", player);
        for (ItemData itemData : data) {
            switch (itemData.getITEM_ID()) {

                case "PROMPT":
                    builder.addIngredient(itemData.getCHARACTER(), new PlayerJoinRequestPromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest));
                    break;

                case "ACCEPT":
                    if (joinRequest.isAccepted()) {
                        builder.addIngredient(itemData.getCHARACTER(), new FactionPlayerJoinRequestConfirmItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest));
                    }
                    break;

                case "DENY":
                    if (joinRequest.isAccepted()) {
                        builder.addIngredient(itemData.getCHARACTER(), new FactionPlayerJoinRequestDenyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest));
                    }
                    break;

                case "REVOKE":
                    builder.addIngredient(itemData.getCHARACTER(), new FactionPlayerJoinRequestRevoke(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest));
                    break;

                case "BORDER":
                    builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                    break;
            }
        }

        return builder;
    }
}
