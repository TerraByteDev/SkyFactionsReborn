package net.skullian.skyfactions.common.gui.screens.obelisk;

import net.skullian.skyfactions.common.config.types.GUIEnums;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.PaginationItemData;
import net.skullian.skyfactions.common.gui.items.impl.BaseSkyItem;
import net.skullian.skyfactions.common.gui.screens.PaginatedScreen;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO))
public class FactionElectionUI extends PaginatedScreen {
    protected FactionElectionUI(SkyUser player) {
        super(GUIEnums.OBELISK_FACTION_ELECTION_GUI.getPath(), player);
    }

    @Nullable
    @Override
    public BaseSkyItem handleItem(@NotNull ItemData itemData) {
        return null;
    }

    @Nullable
    @Override
    protected BaseSkyItem handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return null;
    }

    @NotNull
    @Override
    protected List<BaseSkyItem> getModels(SkyUser player, ItemData data) {
        return List.of();
    }
}
