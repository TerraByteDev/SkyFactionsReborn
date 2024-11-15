package net.skullian.skyfactions.gui.screens.obelisk.invites;

import java.util.ArrayList;
import java.util.List;

import net.skullian.skyfactions.config.types.GUIEnums;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.data.PaginationItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.PaginationBackItem;
import net.skullian.skyfactions.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.invites.PlayerFactionInvitePaginationItem;
import net.skullian.skyfactions.util.ErrorUtil;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

public class PlayerIncomingInvites {

    public static void promptPlayer(Player player) {
        SkyFactionsReborn.databaseManager.factionInvitesManager.getInvitesOfPlayer(Bukkit.getOfflinePlayer(player.getUniqueId())).whenComplete((inviteData, ex) -> {
            Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
                if (ex != null) {
                    ErrorUtil.handleError(player, "get your invites", "SQL_INVITE_GET", ex);
                    return;
                }

                try {
                    GUIData data = GUIAPI.getGUIData(GUIEnums.OBELISK_PLAYER_INCOMING_INVITES_GUI.getPath(), player);
                    PagedGui.Builder gui = registerItems(PagedGui.items()
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
                    Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open the incoming faction invites GUI", "debug", "GUI_LOAD_EXCEPTION");
                }
            });
        });
    }

    private static PagedGui.Builder registerItems(PagedGui.Builder builder, Player player, List<InviteData> inviteData) {
        try {
            builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);
            List<ItemData> data = GUIAPI.getItemData(GUIEnums.OBELISK_PLAYER_INCOMING_INVITES_GUI.getPath(), player);
            List<PaginationItemData> paginationData = GUIAPI.getPaginationData(player);

            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {
                    case "PROMPT", "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;

                    case "MODEL":
                        builder.setContent(getItems(player, itemData, inviteData));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), "player", player));
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

    private static List<Item> getItems(Player player, ItemData itemData, List<InviteData> data) {
        List<Item> items = new ArrayList<>();
        for (InviteData inviteData : data) {
            itemData.setNAME(itemData.getNAME().replace("<faction_name>", inviteData.getFactionName()));
            items.add(new PlayerFactionInvitePaginationItem(itemData, GUIAPI.createItem(itemData, inviteData.getInviter().getUniqueId()), player, inviteData));
        }

        return items;
    }

}
