package net.skullian.skyfactions.gui.obelisk.invites;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.faction.JoinRequestData;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestConfirmItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestDenyItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.FactionPlayerJoinRequestRevoke;
import net.skullian.skyfactions.gui.items.obelisk.invites.InvitePromptItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

public class PlayerOutgoingRequestManageUI {

    public static void promptPlayer(Player player, JoinRequestData joinRequest) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            try {
                GUIData data = GUIAPI.getGUIData("obelisk/invites/player_join_request");
                Gui.Builder.Normal gui = registerItems(Gui.normal()
                        .setStructure(data.getLAYOUT()), player, joinRequest);

                Window window = Window.single()
                        .setViewer(player)
                        .setTitle(TextUtility.color(data.getTITLE(), player))
                        .setGui(gui)
                        .build();

                SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
                window.open();
            } catch (IllegalArgumentException error) {
                error.printStackTrace();
                Messages.ERROR.send(player, "%operation%", "manage your outgoing join request", "%debug%", "GUI_LOAD_EXCEPTION");
            }
        });
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, JoinRequestData joinRequest) {

        List<ItemData> data = GUIAPI.getItemData("obelisk/invites/player_join_request", player);
        for (ItemData itemData : data) {
            switch (itemData.getITEM_ID()) {

                case "PROMPT":
                    builder.addIngredient(itemData.getCHARACTER(), new InvitePromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), joinRequest.getFactionName()));
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
                    builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                    break;
            }
        }

        return builder;
    }
}
