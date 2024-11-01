package net.skullian.skyfactions.gui.obelisk;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.EmptyItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.rune_submit.RuneSubmitItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.window.Window;

public class RunesSubmitUI {

    private VirtualInventory INVENTORY;

    public void promptPlayer(Player player, String type) {
        try {
            GUIData data = GUIAPI.getGUIData("runes_ui", player);
            Gui.Builder.Normal gui = registerItems(Gui.normal()
                    .setStructure(data.getLAYOUT()), player, type, data);

            Window window = Window.single()
                    .setViewer(player)
                    .setTitle(TextUtility.legacyColor(data.getTITLE(), PlayerHandler.getLocale(player.getUniqueId()), player))
                    .addCloseHandler(new Runnable() {
                        @Override
                        public void run() {
                            for (ItemStack item : Arrays.asList(INVENTORY.getItems())) {
                                if (item == null || item.getType().equals(Material.AIR)) return;
                                Map<Integer, ItemStack> map = player.getInventory().addItem(item);

                                // if the player's inventory is full, drop the item.
                                for (ItemStack stack : map.values()) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), stack);
                                }
                            }
                        }
                    })
                    .setGui(gui)
                    .build();

            SoundUtil.playSound(player, data.getOPEN_SOUND(), data.getOPEN_PITCH(), 1f);
            window.open();
        } catch (IllegalArgumentException error) {
            error.printStackTrace();
            Messages.ERROR.send(player, PlayerHandler.getLocale(player.getUniqueId()), "operation", "open your runes submit GUI", "debug", "GUI_LOAD_EXCEPTION");
        }
    }


    private Gui.Builder.Normal registerItems(Gui.Builder.Normal builder, Player player, String type, GUIData guiData) {
        try {
            // yes very rudimentary
            int invSize = 0;
            for (String row : guiData.getLAYOUT()) {
                invSize += row.length() - row.replaceAll(".", "").length();
            }

            VirtualInventory inventory = new VirtualInventory(invSize);
            builder.addIngredient('.', inventory);
            this.INVENTORY = inventory;

            //inventory.setPreUpdateHandler((handler) -> {
            //    handler.setCancelled(RunesAPI.isStackProhibited(handler.getNewItem(), player));
            //});

            List<ItemData> data = GUIAPI.getItemData("runes_ui", player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "PROMPT", "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new EmptyItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), player));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), type, player));
                        break;

                    case "SUBMIT":
                        builder.addIngredient(itemData.getCHARACTER(), new RuneSubmitItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), type, inventory, player));
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
