package net.skullian.skyfactions.core.gui.screens.obelisk;

import net.skullian.skyfactions.core.config.types.GUIEnums;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.data.PaginationItemData;
import net.skullian.skyfactions.core.gui.screens.PaginatedScreen;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.Item;

import java.util.List;

// TODO))
public class FactionElectionUI extends PaginatedScreen {
    protected FactionElectionUI(Player player) {
        super(player, GUIEnums.OBELISK_FACTION_ELECTION_GUI.getPath());
    }

    @Nullable
    @Override
    protected Item handleItem(@NotNull ItemData itemData) {
        return null;
    }

    @Nullable
    @Override
    protected Item handlePaginationItem(@NotNull PaginationItemData paginationItem) {
        return null;
    }

    @NotNull
    @Override
    protected List<Item> getModels(Player player, ItemData data) {
        return List.of();
    }
}
