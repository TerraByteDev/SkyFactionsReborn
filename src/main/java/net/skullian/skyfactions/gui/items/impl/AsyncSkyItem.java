package net.skullian.skyfactions.gui.items.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

import lombok.Getter;
import net.skullian.skyfactions.config.types.ObeliskConfig;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AsyncItem;

@Getter
public abstract class AsyncSkyItem extends AsyncItem {

    private ItemData DATA;
    private ItemStack STACK;
    private Player PLAYER;

    public AsyncSkyItem(ItemData data, ItemStack stack, Player player, Object... replacements) {
        super(
            ObeliskConfig.getLoadingItem(),
            () -> {
                return new ItemProvider() {
                    @Override
                    public @NotNull ItemStack get(@Nullable String s) {
                        ItemBuilder builder = new ItemBuilder(stack)
                            .setDisplayName(replace(TextUtility.color(data.getNAME()), replacements));

                        return builder.get();   
                    }
                };
            }
        );

        this.DATA = data;
        this.STACK = stack;
        this.PLAYER = player;
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



}