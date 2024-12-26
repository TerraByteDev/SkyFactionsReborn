package net.skullian.skyfactions.paper.event.armor;

import com.github.puregero.multilib.MultiLib;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static net.skullian.skyfactions.paper.event.armor.ArmorEvent.ArmorAction;

@SuppressWarnings("ConstantConditions")
public class ArmorListener implements Listener {

    private ArmorEquipEvent callEquip(Player entity, ItemStack item, EquipmentSlot slot, ArmorAction action) {
        ArmorEquipEvent event = new ArmorEquipEvent(entity, item, slot, action);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    private ArmorUnequipEvent callUnequip(Player entity, ItemStack item, EquipmentSlot slot, ArmorAction action) {
        ArmorUnequipEvent event = new ArmorUnequipEvent(entity, item, slot, action);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    private boolean isNotPlayerInventory(Inventory inv) {
        return inv.getContents().length != 5 || inv.getType() == InventoryType.HOPPER;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onInventoryDrag(InventoryDragEvent e) {
        if (isNotPlayerInventory(e.getInventory())) return;

        final EquipmentSlot equipmentSlot = e.getOldCursor().getType().getEquipmentSlot();
        // Check if dragged item is not armor.
        if (equipmentSlot.ordinal() < 2) return;

        for (int rawSlot : e.getRawSlots()) {
            // Check if dragged item is in correct armor slot.
            // RawArmorSlot (Head: 5, Chest: 6, Leggings: 7, Feet: 8)
            // EquipmentOrdinal (Head: 5, Chest: 4, Leggings: 3, Feet: 2)
            if (rawSlot - 10 + equipmentSlot.ordinal() == 0) {
                // We use 'getView()' instead of 'getInventory()' because it's returning the inventory before the drag.
                if (e.getView().getItem(rawSlot).getType().isAir()) {
                    callEquip((Player)e.getWhoClicked(), e.getOldCursor(), equipmentSlot, ArmorAction.DRAG).updateCancellable(e);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || isNotPlayerInventory(e.getInventory())) return;

        final ItemStack clicked = e.getCurrentItem();
        final ItemStack holding = e.getCursor();
        if (clicked == null || holding == null) return;

        final EquipmentSlot clickedEquipment = clicked.getType().getEquipmentSlot();
        final EquipmentSlot holdingEquipment = holding.getType().getEquipmentSlot();
        final Player p = (Player) e.getWhoClicked();

        // Collect to cursor begins to collect from 0 rawSlot. So we first check the player's crafting grid and then
        // the armor slots if they have an item to collect.
        // See: https://wiki.vg/Inventory#Player_Inventory
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.NOTHING) {
            // Keep track of the collected amount of the item.
            int collectedAmount = holding.getAmount();

            // Check upper inventory (CraftingInventory) for items to collect.
            for (ItemStack item : e.getInventory().getContents()) {
                if (holding.isSimilar(item)) {
                    collectedAmount += item.getAmount();
                }
            }

            if (collectedAmount >= holding.getMaxStackSize()) return;

            // Check armor contents from head to feet.
            final ItemStack[] armor = p.getInventory().getArmorContents();
            for (int i = armor.length - 1; i >= 0; i--) {
                if (holding.isSimilar(armor[i])) {
                    if (collectedAmount + armor[i].getAmount() > holding.getMaxStackSize()) return;

                    ArmorEvent armorEvent = callUnequip(p, armor[i].clone(), EquipmentSlot.values()[i + 2], ArmorAction.DOUBLE_CLICK);
                    // Only cancel the armor to collect.
                    if (armorEvent.isCancelled()) {
                        holding.setAmount(holding.getAmount() - armorEvent.getItem().getAmount());
                        MultiLib.getGlobalRegionScheduler().run(SkyFactionsReborn.getInstance(), (consumer) -> {
                            p.getInventory().setItem(armorEvent.getArmorSlot(), armorEvent.getItem());
                            p.updateInventory();
                        });
                    } else {
                        collectedAmount += armor[i].getAmount();
                    }
                }
            }
            return;
        }

        if (e.getSlotType() == InventoryType.SlotType.CONTAINER || e.getSlotType() == InventoryType.SlotType.QUICKBAR) {
            // Shift click equip from player's inventory.
            if (e.isShiftClick()) {
                if (clickedEquipment.ordinal() < 2) return;

                // If armor equipment slot is empty, then we can equip the clicked item.
                if (p.getInventory().getItem(clickedEquipment) == null) {
                    ArmorEvent armorEvent = callEquip(p, clicked, clickedEquipment, ArmorAction.SHIFT_CLICK);
                    // Move clicked item to other inventory when cancelled.
                    if (armorEvent.isCancelled()) {
                        p.getInventory().setItem(armorEvent.getArmorSlot(), armorEvent.getItem());
                        MultiLib.getAsyncScheduler().runNow(SkyFactionsReborn.getInstance(), (consumer) -> {
                            p.getInventory().setItem(armorEvent.getArmorSlot(), null);
                            p.updateInventory();
                        });
                    }
                }
            }
        } else if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
            final EquipmentSlot armorSlot = EquipmentSlot.values()[e.getSlot() - 34];

            if (e.isRightClick() || e.isLeftClick()) {
                if (e.isShiftClick()) {
                    // If empty slot is found, we can unequip the item.
                    if (p.getInventory().firstEmpty() != -1) {
                        callUnequip(p, clicked, armorSlot, ArmorAction.SHIFT_CLICK).updateCancellable(e);
                        return;
                    }

                    // If not and max stack size is 1 the item can't be stacked,
                    // so it also can not be unequipped.
                    // This will happen most of the time because armor is usually equipped.
                    if (clicked.getMaxStackSize() == 1) return;

                    // But if we have a skeleton head for example or any other item or block which has a higher max
                    // stack size, we have to check if the player's inventory contains a similar item.
                    for (ItemStack item : p.getInventory().getStorageContents()) {
                        if (!clicked.isSimilar(item)) continue;

                        // And if the amount of the equipped (clicked) plus the amount of the item in the inventory
                        // is not above the max stack size we can FULLY unequipped it.
                        if (clicked.getAmount() + item.getAmount() <= clicked.getMaxStackSize()) {
                            callUnequip(p, clicked, armorSlot, ArmorAction.SHIFT_CLICK).updateCancellable(e);
                            break;
                        }
                    }
                    return;
                }

                if (!clicked.getType().isAir()) {
                    if (clicked.getAmount() > 1) {
                        // Check if the item is halved. In Creative 'holding' (e.getCursor()) has the result of the
                        // event, so we use it to check.
                        if (e.getClick() == ClickType.CREATIVE && (holding.getAmount() > 1 || (clicked.getAmount() == 2 && holding.isSimilar(clicked)))) return;
                        else if (holding.getType().isAir() && e.isRightClick()) return;
                    }
                    callUnequip(p, clicked, armorSlot, ArmorAction.CLICK).updateCancellable(e);
                }
                if (holdingEquipment == armorSlot) {
                    callEquip(p, holding, armorSlot, ArmorAction.CLICK).updateCancellable(e);
                }

            } else if (e.getAction() == InventoryAction.HOTBAR_SWAP) {
                final ItemStack item;

                if (e.getHotbarButton() == -1) {
                    item = p.getInventory().getItem(EquipmentSlot.OFF_HAND);
                } else {
                    item = p.getInventory().getItem(e.getHotbarButton());
                }

                if (!clicked.getType().isAir()) {
                    callUnequip(p, clicked, armorSlot, ArmorAction.HOTBAR_SWAP).updateCancellable(e);
                }
                if (item != null) {
                    if (item.getType().getEquipmentSlot() == armorSlot) {
                        callEquip(p, item, armorSlot, ArmorAction.HOTBAR_SWAP).updateCancellable(e);
                    }
                }
            } else if (holding.getType().isAir()) {
                if (e.getClick() == ClickType.DROP) {
                    if (clicked.getAmount() == 1) {
                        callUnequip(p, clicked, armorSlot, ArmorAction.DROP).updateCancellable(e);
                    }
                } else if (e.getClick() == ClickType.CONTROL_DROP) {
                    callUnequip(p, clicked, armorSlot, ArmorAction.DROP).updateCancellable(e);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if (!e.hasItem()) return;

        final ItemStack useItem = e.getItem().clone();
        final EquipmentSlot equipmentSlot = useItem.getType().getEquipmentSlot();
        if (equipmentSlot.ordinal() < 2) return;

        final Player p = e.getPlayer();
        if (p.getInventory().getItem(equipmentSlot) != null) return;

        MultiLib.getGlobalRegionScheduler().run(SkyFactionsReborn.getInstance(), (consumer) -> {
            if (p.getInventory().getItem(equipmentSlot) != null) {
                ArmorEvent armorEvent = callEquip(p, useItem, equipmentSlot, ArmorAction.HOTBAR);
                // When cancelled unequip item in armor slot and put it back in the hand of the player.
                if (armorEvent.isCancelled()) {
                    p.getInventory().setItem(armorEvent.getArmorSlot(), null);
                    p.getInventory().setItem(e.getHand(), armorEvent.getItem());
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockDispenseArmor(BlockDispenseArmorEvent e) {
        if (e.getTargetEntity() instanceof Player) {
            callEquip((Player)e.getTargetEntity(), e.getItem(), e.getItem().getType().getEquipmentSlot(), ArmorAction.DISPENSED).updateCancellable(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerItemBreak(PlayerItemBreakEvent e) {
        final ItemStack item = e.getBrokenItem();
        if (item.getType().getEquipmentSlot().ordinal() < 2 || item.getAmount() > 1) return;

        callUnequip(e.getPlayer(), item, item.getType().getEquipmentSlot(), ArmorAction.BROKE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getKeepInventory()) return;

        final Player p = e.getEntity();
        final ItemStack[] armor = p.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] == null) continue;
            callUnequip(p, armor[i], EquipmentSlot.values()[i + 2], ArmorAction.DEATH);
        }
    }
}