package net.skullian.skyfactions.gui.obelisk;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.skyfactions.gui.items.GeneralPromptItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.rune_submit.RuneSubmitItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class RunesSubmitUI {

    public VirtualInventory INVENTORY;

    public void promptPlayer(Player player, String type) {
        try {
            GUIData data = GUIAPI.getGUIData("runes_ui");
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player, type, data);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.color(data.getTITLE()))
                    .setCloseable(false)
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, "%operation%", "open your runes submit GUI", "%debug%", "GUI_LOAD_EXCEPTION");
        }
    }


    private Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, String type, GUIData guiData) {
        try {
            // yes very rudimentary
            int invSize = 0;
            for (String row : guiData.getLAYOUT()) {
                invSize += row.length() - row.replace(".", "").length();
            }

            VirtualInventory inventory = new VirtualInventory(invSize);
            this.INVENTORY = inventory;
            builder.addIngredient('.', inventory);

            inventory.setPreUpdateHandler((handler) -> {
                handler.setCancelled(RunesAPI.isStackProhibited(handler.getNewItem(), player));
            });

            List<ItemData> data = GUIAPI.getItemData("runes_ui", player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralPromptItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), type));
                        break;

                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId())));
                        break;

                    case "SUBMIT":
                        builder.addIngredient(itemData.getCHARACTER(), new RuneSubmitItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), type, INVENTORY.getItems() /* todo */));
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
