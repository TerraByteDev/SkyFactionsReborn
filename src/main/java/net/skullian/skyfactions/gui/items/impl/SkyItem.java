package net.skullian.skyfactions.gui.items.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

@Getter
public abstract class SkyItem extends AbstractItem {

    private ItemData DATA;
    private ItemStack STACK;
    private Player PLAYER;

    public SkyItem(ItemData data, ItemStack stack, Player player) {
        this.DATA = data;
        this.STACK = stack;
        this.PLAYER = player;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
            .setDisplayName(replace(TextUtility.color(DATA.getNAME())));

        for (String loreLine : DATA.getLORE()) {
            builder.addLoreLines(replace(TextUtility.color(loreLine), replacements()));
        }

        return process(builder);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!DATA.getSOUND().equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, DATA.getSOUND(), DATA.getPITCH(), 1f);
        }

        onClick(clickType, player, event);
    }

    public ItemBuilder process(ItemBuilder builder) { return builder; }

    public Object[] replacements() { return new Object[0]; }

    public static String replace(String message, Object... replacements) {
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(String.valueOf(replacements[i]), String.valueOf(replacements[i + 1]));
        }

        return message;
    }

    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {}
}