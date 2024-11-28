package net.skullian.skyfactions.common.gui.screens;

import lombok.Getter;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.gui.data.GUIData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public abstract class PaginatedScreen {
    protected final String guiPath;
    protected final GUIData guIData;

    public PaginatedScreen(String guiPath, SkyUser player) {
        this.guiPath = guiPath;
        this.guIData = GUIAPI.getGUIData(guiPath, player);
    }

    protected abstract void init();

    public abstract void show();

    protected abstract @Nullable BaseSkyItem handleItem(@NotNull ItemData itemData);

    protected abstract void registerItems();

    protected abstract @Nullable BaseSkyItem handlePaginationItem(@NotNull PaginationItemData paginationItem);

    protected abstract @NotNull List<BaseSkyItem> getModels(SkyUser player, ItemData data);
}
