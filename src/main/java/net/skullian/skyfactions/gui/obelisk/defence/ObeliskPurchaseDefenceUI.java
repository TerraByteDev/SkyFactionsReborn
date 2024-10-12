package net.skullian.skyfactions.gui.obelisk.defence;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.defence.struct.DefenceStruct;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.skyfactions.gui.items.GeneralCancelItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.obelisk.defence.ObeliskConfirmPurchaseItem;
import net.skullian.skyfactions.gui.items.obelisk.defence.ObeliskPaginatedDefenceItem;
import net.skullian.skyfactions.util.SLogger;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class ObeliskPurchaseDefenceUI {

    public static void promptPlayer(Player player, String obeliskType, DefenceStruct struct, Faction faction) {
        try {
            GUIData data = GUIAPI.getGUIData("obelisk/defences/purchase_defence");
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player, obeliskType, struct, faction);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "open the defence purchase confirmation GUI", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }

    private static Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, String obeliskType, DefenceStruct struct, Faction faction) {
        try {
            List<ItemData> data = GUIAPI.getItemData("obelisk/defences/purchase_defence", player);
            for (ItemData itemData : data) {
                SLogger.info(itemData.getITEM_ID());

                SLogger.info(itemData.getCHARACTER());
                switch (itemData.getITEM_ID()) {

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), obeliskType));
                        break;

                    case "CONFIRM":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskConfirmPurchaseItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), obeliskType, struct, faction, player));
                        break;

                    case "CANCEL":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralCancelItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;

                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;

                    case "DEFENCE":
                        itemData.setNAME(struct.getNAME());
                        itemData.setBASE64_TEXTURE(struct.getITEM_SKULL());
                        itemData.setMATERIAL(struct.getITEM_MATERIAL());
                        itemData.setLORE(struct.getITEM_LORE());

                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskPaginatedDefenceItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), struct, false, obeliskType, faction));
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
