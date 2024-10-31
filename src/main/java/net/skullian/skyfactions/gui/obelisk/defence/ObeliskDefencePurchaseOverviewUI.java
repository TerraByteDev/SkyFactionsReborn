package net.skullian.skyfactions.gui.obelisk.defence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.DefencesFactory;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.data.PaginationItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.PaginationBackItem;
import net.skullian.skyfactions.gui.items.PaginationForwardItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.defence.ObeliskPaginatedDefenceItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

public class ObeliskDefencePurchaseOverviewUI {

    public static void promptPlayer(Player player, String obeliskType, Faction faction) {
        try {
            GUIData data = GUIAPI.getGUIData("obelisk/defences/defence_purchase_overview", player);
            PagedGui.Builder gui = registerItems(PagedGui.items()
                    .setStructure(data.getLAYOUT()), player, obeliskType, faction);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(data.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, player.locale().getLanguage(), "%operation%", "open the defences purchase GUI", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static PagedGui.Builder registerItems(PagedGui.Builder builder, Player player, String obeliskType, Faction faction) {
        try {
            builder.addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL);
            List<ItemData> data = GUIAPI.getItemData("obelisk/defences/defence_purchase_overview", player);
            List<PaginationItemData> paginationData = GUIAPI.getPaginationData(player);

            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {
                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), obeliskType));
                        break;

                    case "MODEL":
                        builder.setContent(getItems(player, itemData, obeliskType, faction));
                        break;

                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
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

    private static List<Item> getItems(Player player, ItemData data, String obeliskType, Faction faction) {
        List<Item> items = new ArrayList<>();

        for (Map.Entry<String, DefenceStruct> defence : DefencesFactory.defences.getOrDefault(PlayerHandler.getLocale(player.getUniqueId()), DefencesFactory.getDefaultStruct()).entrySet()) {
            DefenceStruct struct = defence.getValue();
            data.setNAME(struct.getNAME());
            data.setBASE64_TEXTURE(struct.getITEM_SKULL());
            data.setMATERIAL(struct.getITEM_MATERIAL());
            data.setLORE(struct.getITEM_LORE());

            items.add(new ObeliskPaginatedDefenceItem(data, GUIAPI.createItem(data, player.getUniqueId()), struct, true, obeliskType, faction));
        }
        return items;
    }
}
