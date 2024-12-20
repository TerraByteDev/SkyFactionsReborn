package net.skullian.skyfactions.paper.event.armor;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called when a Player is equipping armor.
 */
public class ArmorEquipEvent extends ArmorEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    protected ArmorEquipEvent(@NotNull final Player who, @NotNull final ItemStack item,
                              @NotNull final EquipmentSlot slot, @NotNull final ArmorAction action) {
        super(who, item, slot, action);
    }

    public ArmorEquipEvent(@NotNull final Player who, @NotNull final ItemStack item,
                           @NotNull final EquipmentSlot slot) {
        this(who, item, slot, ArmorAction.CUSTOM);
    }

    public ArmorEquipEvent(@NotNull final Player who, @NotNull final ItemStack item) {
        this(who, item, item.getType().getEquipmentSlot());
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}