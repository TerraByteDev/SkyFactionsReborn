package net.skullian.torrent.skyfactions.gui.obelisk.invites;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.db.InviteData;
import net.skullian.torrent.skyfactions.gui.data.GUIData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.data.PaginationItemData;
import net.skullian.torrent.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.torrent.skyfactions.gui.items.GeneralPromptItem;
import net.skullian.torrent.skyfactions.gui.items.PaginationBackItem;
import net.skullian.torrent.skyfactions.gui.items.PaginationForwardItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.invites.FactionJoinRequestPaginationItem;
import net.skullian.torrent.skyfactions.util.ErrorHandler;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class JoinRequestsUI {

    public static void promptPlayer(Player player) {
        try {
            GUIData data = GUIAPI.getGUIData("obelisk/invites/incoming_requests");
            PagedGui.Builder gui = registerItems(PagedGui.items()
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
            Messages.ERROR.send(player, "%operation%", "open the faction join requests GUI", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static PagedGui.Builder registerItems(PagedGui.Builder builder, Player player) {
        try {
            builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);
            List<ItemData> data = GUIAPI.getItemData("obelisk/invites/incoming_requests", player);
            List<PaginationItemData> paginationData = GUIAPI.getPaginationData(player);

            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {
                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralPromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;

                    case "MODEL":
                        builder.setContent(getItems(player, itemData));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "faction"));
                        break;

                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;
                }
            }
            for (PaginationItemData paginationItem : paginationData) {
                switch (paginationItem.getITEM_ID()) {

                    case "FORWARD_BUTTON":
                        builder.addIngredient(paginationItem.getCHARACTER(), new PaginationForwardItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId())));
                        break;

                    case "BACK_BUTTON":
                        builder.addIngredient(paginationItem.getCHARACTER(), new PaginationBackItem(paginationItem, GUIAPI.createItem(paginationItem, player.getUniqueId())));
                        break;
                }
            }

            return builder;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        return builder;
    }

    private static List<Item> getItems(Player player, ItemData itemData) {
        List<Item> items = new ArrayList<>();
        FactionAPI.getFaction(player).whenComplete((faction, exc) -> {
            if (exc != null) {
                ErrorHandler.handleError(player, "open the faction join requests GUI", "FACTION_NOT_FOUND", exc);
                return;
            }

            if (faction == null) {
                Messages.ERROR.send(player, "%operation%", "open the faction join requests GUI", "%debug%", "FACTION_NOT_FOUND");
                return;
            }
            faction.getJoinRequests().whenComplete((data, ex) -> {
                if (ex != null) {
                    ErrorHandler.handleError(player, "open the faction join requests GUI", "FACTION_NOT_FOUND", exc);
                    return;
                }

                for (InviteData inviteData : data) {
                    itemData.setNAME(itemData.getNAME().replace("%player_name%", inviteData.getPlayer().getName()));
                    items.add(new FactionJoinRequestPaginationItem(itemData, GUIAPI.createItem(itemData, inviteData.getPlayer().getUniqueId()), inviteData.getPlayer(), inviteData));
                }
            });
        });

        return items;
    }
}
