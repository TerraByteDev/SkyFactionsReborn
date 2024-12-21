package net.skullian.skyfactions.common.gui.items.impl;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.text.TextUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Getter
@Setter
public abstract class SkyPageItem extends BaseSkyItem {

    private int currentPage;
    private int pageAmount;

    public SkyPageItem(ItemData data, SkyItemStack stack, SkyUser player, Object[] optionals, PaginationItemData paginationItemData, boolean forward) {
        super(data, stack, player, Arrays.asList(optionals, paginationItemData, forward).toArray(), false);
    }

    @Override
    public SkyItemStack getItemStack() {
        Object[] replacements = replacements();
        PaginationItemData paginationItemData = (PaginationItemData) getOPTIONALS()[1];

        return SkyItemStack.builder()
                .material(paginationItemData.getMATERIAL())
                .amount(1)
                .textures(paginationItemData.getBASE64_TEXTURE())
                .displayName(Messages.replace(paginationItemData.getNAME(), getPLAYER(), replacements))
                .lore(new ArrayList<>(Collections.singleton(hasNextPage()
                        ? Messages.replace(paginationItemData.getMORE_PAGES_LORE().replaceAll("next_page", String.valueOf(getCurrentPage() + 2)).replace("total_pages", String.valueOf(getPageAmount())), getPLAYER(), replacements)
                        : Messages.replace(paginationItemData.getNO_PAGES_LORE(), getPLAYER(), replacements))))
                .build();
    }

    public boolean hasNextPage() {
        return getCurrentPage() < getPageAmount() - 1;
    }
}
