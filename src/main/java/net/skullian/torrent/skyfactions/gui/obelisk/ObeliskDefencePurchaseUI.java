package net.skullian.torrent.skyfactions.gui.obelisk;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.defence.DefencesRegistry;
import net.skullian.torrent.skyfactions.defence.struct.DefenceStruct;
import net.skullian.torrent.skyfactions.gui.data.GUIData;
import net.skullian.torrent.skyfactions.gui.data.ItemData;
import net.skullian.torrent.skyfactions.gui.data.PaginationItemData;
import net.skullian.torrent.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.torrent.skyfactions.gui.items.GeneralPromptItem;
import net.skullian.torrent.skyfactions.gui.items.PaginationBackItem;
import net.skullian.torrent.skyfactions.gui.items.PaginationForwardItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.torrent.skyfactions.gui.items.obelisk.defence.ObeliskPaginatedDefenceItem;
import net.skullian.torrent.skyfactions.util.SoundUtil;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObeliskDefencePurchaseUI {

    public static void promptPlayer(Player player, String obeliskType) {
        try {
            GUIData data = GUIAPI.getGUIData("obelisk/defences/defence_purchase");
            PagedGui.Builder gui = registerItems(PagedGui.items()
                    .setStructure(data.getLAYOUT()), player, obeliskType);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "open the defences purchase GUI", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static PagedGui.Builder registerItems(PagedGui.Builder builder, Player player, String obeliskType) {
        try {
            builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);
            List<ItemData> data = GUIAPI.getItemData("obelisk/defences/defence_purchase", player);
            List<PaginationItemData> paginationData = GUIAPI.getPaginationData(player);

            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {
                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralPromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), obeliskType));
                        break;

                    case "MODEL":
                        builder.setContent(getItems(player, itemData));
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
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
        }

        return builder;
    }

    private static List<Item> getItems(Player player, ItemData data) {
        List<Item> items = new ArrayList<>();


        for (Map.Entry<String, DefenceStruct> defence : DefencesRegistry.defences.entrySet()) {
            DefenceStruct struct = defence.getValue();
            data.setNAME(struct.getNAME());
            data.setBASE64_TEXTURE(struct.getITEM_SKULL());
            data.setMATERIAL(struct.getITEM_MATERIAL());
            data.setLORE(struct.getITEM_LORE());

            items.add(new ObeliskPaginatedDefenceItem(data, GUIAPI.createItem(data, player.getUniqueId()), defence.getValue()));
        }
        return items;
    }
}
