package net.skullian.skyfactions.gui.items;

import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class GeneralBorderItem extends SkyItem {

    public GeneralBorderItem(ItemData data, ItemStack stack) {
        super(data, stack, null);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!getDATA().getSOUND().equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, getDATA().getSOUND(), getDATA().getPITCH(), 1);
        }
    }

    @Override
    public ItemBuilder process(ItemBuilder builder) { return builder; } // no extra modifications needed

    @Override
    public Object[] replacements() { return new Object[0]; }

}
