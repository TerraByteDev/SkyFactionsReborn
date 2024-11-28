package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.util.text.TextUtility;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.invites.JoinRequestManageUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FactionJoinRequestPaginationItem extends SkyItem {

    private InviteData DATA;

    public FactionJoinRequestPaginationItem(ItemData data, ItemStack stack, Player player, InviteData inviteData) {
        super(data, stack, player, List.of(inviteData).toArray());

        this.DATA = inviteData;
    }

    @Override
    public Object[] replacements() {
        InviteData data = (InviteData) getOptionals()[1];

        return List.of(
            "player_name", data.getPlayer().getName(),
            "timestamp", TextUtility.formatExtendedElapsedTime(data.getTimestamp())
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        JoinRequestManageUI.promptPlayer(player, DATA);
    }


}
