package net.skullian.skyfactions.common.gui.items.impl;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.api.SkyApi;
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
        PaginationItemData paginationItemData = (PaginationItemData) getOPTIONALS()[1];
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(getPLAYER().getUniqueId());

        return SkyItemStack.builder()
                .displayName(TextUtility.legacyColor(paginationItemData.getNAME(), locale, getPLAYER()))
                .lore(new ArrayList<>(Collections.singleton(hasNextPage()
                        ? TextUtility.legacyColor(paginationItemData.getMORE_PAGES_LORE().replaceAll("next_page", String.valueOf(getCurrentPage() + 2)).replace("total_pages", String.valueOf(getPageAmount())), locale, getPLAYER())
                        : TextUtility.legacyColor(paginationItemData.getNO_PAGES_LORE(), locale, getPLAYER()))))
                .build();
    }

    public boolean hasNextPage() {
        return getCurrentPage() < getPageAmount() - 1;
    }
}
