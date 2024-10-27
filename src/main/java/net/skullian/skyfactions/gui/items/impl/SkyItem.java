package net.skullian.skyfactions.gui.items.impl;

import org.bukkit.inventory.ItemStack;

import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.util.text.TextUtility;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public abstract class SkyItem extends AbstractItem {

    private ItemData DATA;
    private ItemStack STACK;

    public SkyItem(ItemData data, ItemStack stack) {
        this.DATA = data;
        this.STACK = stack;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemBuilder builder = new ItemBuilder(STACK)
            .setDisplayName(TextUtility.color(DATA.getNAME()));

        for (String loreLine : DATA.getLORE()) {
            builder.addLoreLines(TextUtility.color(loreLine));
        }

        return process(builder);
    }

    public abstract ItemBuilder process(ItemBuilder builder);



}