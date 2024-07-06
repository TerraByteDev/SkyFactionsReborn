package net.skullian.torrent.skyfactions.util.gui;

import net.skullian.torrent.skyfactions.api.GUIAPI;
import net.skullian.torrent.skyfactions.api.RunesAPI;
import net.skullian.torrent.skyfactions.util.gui.data.GUIData;
import net.skullian.torrent.skyfactions.util.gui.data.ItemData;
import net.skullian.torrent.skyfactions.util.gui.items.GeneralBorderItem;
import net.skullian.torrent.skyfactions.util.gui.items.obelisk.ObeliskBackItem;
import net.skullian.torrent.skyfactions.util.gui.items.rune_submit.RunePromptItem;
import net.skullian.torrent.skyfactions.util.gui.items.rune_submit.RuneSubmitItem;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;
import java.util.List;

public class RuneSubmitUI extends AbstractGui {

    private static AbstractGui GUI;
    private static GUIData DATA;
    private static Inventory INVENTORY;

    public RuneSubmitUI(Structure structure, GUIData data) {
        super(structure.getWidth(), structure.getHeight());
        GUI = this;
        GUI.applyStructure(structure);
        DATA = data;
    }

    @Override
    public void handleClick(int slotNumber, Player player, ClickType clickType, InventoryClickEvent event) {
        // TODO MIGRATE FROM ABSTRACTGUI TO ADDING INGREDIENT INVENTORY! https://discord.com/channels/859080327096172544/1005893933458538537/1259233825825751142

        SlotElement slotElement = this.getSlotElement(slotNumber);
        if (slotElement instanceof SlotElement.LinkedSlotElement) {
            SlotElement.LinkedSlotElement linkedElement = (SlotElement.LinkedSlotElement)slotElement;
            AbstractGui gui = (AbstractGui)linkedElement.getGui();
            gui.handleClick(linkedElement.getSlotIndex(), player, clickType, event);
        } else if (slotElement instanceof SlotElement.ItemSlotElement) {
            event.setCancelled(true);
            SlotElement.ItemSlotElement itemElement = (SlotElement.ItemSlotElement)slotElement;
            itemElement.getItem().handleClick(clickType, player, event);
        } else if (slotElement instanceof SlotElement.InventorySlotElement) {
            this.handleInvSlotElementClick((SlotElement.InventorySlotElement)slotElement, event);
        } else if (event.getCursor() != null) {
            INVENTORY = event.getClickedInventory();
            event.setCancelled(handleItemEntry(player, event.getCursor()));
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void handleItemShift(InventoryClickEvent event) {
        event.setCancelled(handleItemEntry((Player) event.getWhoClicked(), event.getCurrentItem()));
        INVENTORY = event.getClickedInventory();
    }

    // False = Do not cancel.
    // True = Cancel.
    private boolean handleItemEntry(Player player, ItemStack stack) {
        if (stack != null) {
            return RunesAPI.isStackProhibited(stack, player);
        } else {
            return true;
        }
    }

    public static void promptPlayer(Player player) {

        Window window = Window.single()
                .setViewer(player)
                .setTitle(TextUtility.color(DATA.getTITLE()))
                .setGui(GUI)
                .addCloseHandler(() -> {
                    for (int i = 0; i < INVENTORY.getSize(); i++) {
                        if (GUI.hasSlotElement(i)) return;
                        ItemStack stack = INVENTORY.getItem(i);
                        if (stack != null && !stack.getType().equals(Material.AIR)) {
                            player.getInventory().addItem(INVENTORY.getItem(i));
                        }
                    }
                    })
                .build();


        window.open();
    }

    public static Structure createStructure(Player player, String type, GUIData data) {
        return registerItems(new Structure(data.getLAYOUT()), player, type);
    }

    private static Structure registerItems(Structure builder, Player player, String type) {
        try {
            List<ItemData> data = GUIAPI.getItemData("runes_ui", player);
            for (ItemData itemData : data) {
                switch (itemData.getITEM_ID()) {

                    case "PROMPT":
                        builder.addIngredient(itemData.getCHARACTER(), new RunePromptItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;

                    case "BACK":
                        builder.addIngredient(itemData.getCHARACTER(), new ObeliskBackItem(itemData, GUIAPI.createItem(itemData, player), type));
                        break;

                    case "BORDER":
                        builder.addIngredient(itemData.getCHARACTER(), new GeneralBorderItem(itemData, GUIAPI.createItem(itemData, player)));
                        break;

                    case "SUBMIT":
                        builder.addIngredient(itemData.getCHARACTER(), new RuneSubmitItem(itemData, GUIAPI.createItem(itemData, player), type, GUI));
                        break;
                }
            }

            return builder;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return builder;
    }
}
