package net.skullian.skyfactions.gui.items.impl;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.api.PlayerAPI;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.CooldownManager;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
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
        String locale = PlayerAPI.getLocale(PLAYER.getUniqueId());

        Object[] replacements = replacements();
        ItemBuilder builder = new ItemBuilder(STACK)
                .setDisplayName(TextUtility.legacyColor(DATA.getNAME(), locale, getPLAYER(), replacements));

        for (String loreLine : DATA.getLORE()) {
            builder.addLoreLines(TextUtility.legacyColor(loreLine, locale, getPLAYER(), replacements));
        }

        return process(builder);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (CooldownManager.ITEMS.manage(player)) return;

        if (!DATA.getSOUND().equalsIgnoreCase("none")) {
            SoundUtil.playSound(player, DATA.getSOUND(), DATA.getPITCH(), 1f);
        }

        onClick(clickType, player, event);
    }

    public ItemBuilder process(ItemBuilder builder) {
        return builder;
    }

    public Object[] replacements() {
        return new Object[0];
    }

    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
    }

    public String[] toList(List<String> strings, Object... replacements) {
        String locale = PlayerAPI.getLocale(getPLAYER().getUniqueId());
        List<String> formatted = strings.stream()
                .map(string -> TextUtility.legacyColor(string, locale, getPLAYER(), replacements))
                .collect(Collectors.toList());

        return formatted.toArray(new String[formatted.size()]);
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
}
