package net.skullian.skyfactions.core.gui.items.obelisk.invites;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.core.gui.data.ItemData;
import net.skullian.skyfactions.core.gui.items.impl.old.SkyItem;
import net.skullian.skyfactions.core.gui.screens.obelisk.invites.JoinRequestManageUI;
import net.skullian.skyfactions.common.util.text.TextUtility;

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
