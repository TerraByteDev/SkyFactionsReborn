package net.skullian.skyfactions.gui.items.impl;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.config.types.ObeliskConfig;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.CooldownManager;
import net.skullian.skyfactions.util.SoundUtil;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AsyncItem;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class AsyncSkyItem implements Item {

    private ItemData DATA;
    private ItemStack STACK;
    private Player PLAYER;
    private volatile ItemProvider provider;
    private Object[] optionals;

    public AsyncSkyItem(ItemData data, ItemStack stack, Player player, Object[] optionals) {
        this.optionals = optionals;
        this.DATA = data;
        this.STACK = stack;
        this.PLAYER = player;

        this.provider = ObeliskConfig.getLoadingItem(player);
        Bukkit.getScheduler().runTaskAsynchronously(SkyFactionsReborn.getInstance(), () -> {
            this.provider = new ItemProvider() {
                @Override
                public @NotNull ItemStack get(@Nullable String s) {
                    String locale = PlayerHandler.getLocale(PLAYER.getUniqueId());
                    Object[] replacements = replacements();
                    System.out.println(DATA);
                    ItemBuilder builder = new ItemBuilder(STACK)
                            .setDisplayName(TextUtility.legacyColor(DATA.getNAME(), locale, PLAYER, replacements));

                    for (String loreLine : DATA.getLORE()) {
                        builder.addLoreLines(TextUtility.legacyColor(loreLine, locale, PLAYER, replacements));
                    }

                    return process(builder).get();
                }
            };
            Bukkit.getScheduler().runTask(SkyFactionsReborn.getInstance(), this::notifyWindows);
        });
    }

    @Override
    public ItemProvider getItemProvider() {
        return provider;
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

    public String[] toList(List<String> strings) {
        String locale = PlayerHandler.getLocale(getPLAYER().getUniqueId());
        List<String> formatted = strings.stream()
                .map(string -> TextUtility.legacyColor(string, locale, getPLAYER()))
                .collect(Collectors.toList());

        return formatted.toArray(new String[formatted.size()]);
    }

    // I only have this so I'm not duplicating the sound checks / playing.
    public void onClick(ClickType clickType, Player player, InventoryClickEvent event) {
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
