package net.skullian.skyfactions.gui.obelisk;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GUIAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.gui.data.GUIData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.GeneralBorderItem;
import net.skullian.skyfactions.gui.items.obelisk.ObeliskBackItem;
import net.skullian.skyfactions.gui.items.GeneralPromptItem;
import net.skullian.skyfactions.gui.items.rune_submit.RuneSubmitItem;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RuneSubmitUI extends AbstractGui {

    private AbstractGui GUI;
    private GUIData DATA;
    private Inventory INVENTORY;
    private String TYPE;

    public RuneSubmitUI(Structure structure, GUIData data, Player player, String type) {
        super(structure.getWidth(), structure.getHeight());
        GUI = this;
        GUI.applyStructure(registerItems(structure, player, type));
        DATA = data;
        TYPE = type;
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
            event.setCancelled(handleItemEntry(player, event.getCursor()));
        } else {
            event.setCancelled(true);
        }

        this.INVENTORY = event.getClickedInventory();
    }

    @Override
    public void handleItemShift(InventoryClickEvent event) {
        this.INVENTORY = event.getClickedInventory();
        event.setCancelled(handleItemEntry((Player) event.getWhoClicked(), event.getCurrentItem()));
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

    public void promptPlayer(Player player) {
        Window window = Window.single()
                .setViewer(player)
                .setTitle(TextUtility.color(DATA.getTITLE()))
                .setGui(GUI)
                .addCloseHandler(() -> {
                    handleClose(player);
                    })
                .build();

        player.setMetadata("rune_ui", new FixedMetadataValue(SkyFactionsReborn.getInstance(), true));
        SoundUtil.playSound(player, DATA.getOPEN_SOUND(), DATA.getOPEN_PITCH(), 1f);
        window.open();
    }

    private void handleClose(Player player) {
        if (!player.hasMetadata("rune_ui")) return;
        if (INVENTORY == null) return;

        for (int i = 0; i < INVENTORY.getSize(); i++) {
            if (GUI.hasSlotElement(i)) continue;
            ItemStack stack = INVENTORY.getItem(i);
            if (stack != null || !stack.getType().equals(Material.AIR)) {
                player.getInventory().addItem(stack);
            }
        }

        INVENTORY = null;
    }

    public static Structure createStructure(GUIData data) {
        return new Structure(data.getLAYOUT());
    }

    private Structure registerItems(Structure builder, Player player, String type) {
        try {
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
                        builder.addIngredient(itemData.getCHARACTER(), new RuneSubmitItem(itemData, GUIAPI.createItem(itemData, player.getUniqueId()), type, GUI));
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
