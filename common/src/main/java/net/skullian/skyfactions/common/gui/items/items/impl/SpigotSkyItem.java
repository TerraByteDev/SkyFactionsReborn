package net.skullian.skyfactions.common.gui.items.items.impl;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.CooldownManager;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.api.adapter.SpigotAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SpigotSkyItem extends SkyItem implements Item {

    public SpigotSkyItem(ItemData data, SkyItemStack stack, SkyUser player, Object[] optionals) {
        super(data, stack, player, optionals);
    }

    @Override
    public void update() {
        notifyWindows();
    }

    @Override
    public ItemProvider getItemProvider() {
        return SpigotAdapter.adapt(getSTACK(), getPLAYER());
    }

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

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), () -> {
            event.setCancelled(true);

            if (CooldownManager.ITEMS.manage(player)) return;
            if (!getDATA().getSOUND().equalsIgnoreCase("none")) SkyApi.getInstance().getSoundAPI().playSound(getPLAYER(), getDATA().getSOUND(), getDATA().getPITCH(), 1f);

            onClick(SkyClickType.valueOf(clickType.name()), getPLAYER());
        });
    }
}
