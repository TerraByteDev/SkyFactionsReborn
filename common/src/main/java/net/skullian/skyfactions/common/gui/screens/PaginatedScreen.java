package net.skullian.skyfactions.common.gui.screens;

import lombok.Getter;
import net.skullian.skyfactions.common.api.GUIAPI;
import net.skullian.skyfactions.common.api.SkyApi;
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
    protected final SkyUser player;

    public PaginatedScreen(String guiPath, SkyUser player) {
        this.guiPath = guiPath;
        this.guIData = GUIAPI.getGUIData(guiPath, player);
        this.player = player;
    }

    protected void init() {}

    public void show() {
        SkyApi.getInstance().getUIShower().show(player, this);
    }

    protected abstract @Nullable BaseSkyItem handleItem(@NotNull ItemData itemData);

    protected void registerItems() {};

    protected abstract @Nullable BaseSkyItem handlePaginationItem(@NotNull PaginationItemData paginationItem);

    protected abstract @NotNull List<BaseSkyItem> getModels(SkyUser player, ItemData data);
}
