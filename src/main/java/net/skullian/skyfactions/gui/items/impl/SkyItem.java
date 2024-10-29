package net.skullian.skyfactions.gui.items.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

@Getter
public abstract class SkyItem implements Item {

    private ItemData DATA;
    private ItemStack STACK;
    private Player PLAYER;
    private Object[] optionals;

    public SkyItem(ItemData data, ItemStack stack, Player player, Object[] optionals) {
        this.DATA = data;
        this.STACK = stack;
        this.PLAYER = player;
        this.optionals = optionals;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
            .setDisplayName(replace(TextUtility.color(DATA.getNAME(), getPLAYER())));

        for (String loreLine : DATA.getLORE()) {
            builder.addLoreLines(replace(TextUtility.color(loreLine, getPLAYER()), replacements()));
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

    private final Set<AbstractWindow> windows = new HashSet<>();
    
    @Override
    public void addWindow(AbstractWindow window) {
        windows.add(window);
    }
    
    @Override
    public void removeWindow(AbstractWindow window) {
        windows.remove(window);
    }
    
    @Override
    public Set<Window> getWindows() {
        return Collections.unmodifiableSet(windows);
    }
    
    @Override
    public void notifyWindows() {
        windows.forEach(w -> w.handleItemProviderUpdate(this));
    }
}