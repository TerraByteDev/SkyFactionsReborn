package net.skullian.skyfactions.gui.items.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.ObeliskConfig;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

@Getter
public abstract class AsyncSkyItem implements Item {

    private ItemData DATA;
    private ItemStack STACK;
    private Player PLAYER;
    private ItemProvider provider;

    public AsyncSkyItem(ItemData data, ItemStack stack, Player player, Object... replacements) {
        this.provider = ObeliskConfig.getLoadingItem();
        Bukkit.getScheduler().runTaskAsynchronously(SkyFactionsReborn.getInstance(), () -> {
            this.provider = new ItemProvider() {
                @Override
                public @NotNull ItemStack get(@Nullable String s) {
                    ItemBuilder builder = new ItemBuilder(stack)
                        .setDisplayName(replace(TextUtility.color(data.getNAME()), replacements));

                    return process(builder).get();
                }
            };
        });

        this.DATA = data;
        this.STACK = stack;
        this.PLAYER = player;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (!DATA.getSOUND().equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, DATA.getSOUND(), DATA.getPITCH(), 1f);
        }
    }

    public abstract ItemBuilder process(ItemBuilder builder);

    public abstract Object[] replacements();

    public static String replace(String message, Object... replacements) {
        for (int i = 0; i < replacements.length; i += 2) {
            if (replacements[i + 1] instanceof CompletableFuture<?>) {
                replacements[i + 1] = ((CompletableFuture<?>) replacements[i + 1]).join();
            }
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