package net.skullian.skyfactions.gui.items.obelisk.invites;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.skullian.skyfactions.db.InviteData;
import net.skullian.skyfactions.gui.data.ItemData;
import net.skullian.skyfactions.gui.items.impl.SkyItem;
import net.skullian.skyfactions.gui.obelisk.invites.JoinRequestManageUI;
import net.skullian.skyfactions.util.text.TextUtility;

public class FactionJoinRequestPaginationItem extends SkyItem {

    private InviteData DATA;

    public FactionJoinRequestPaginationItem(ItemData data, ItemStack stack, OfflinePlayer player, InviteData inviteData) {
        super(data, stack, null, List.of(inviteData).toArray());

        this.DATA = inviteData;
    }

    @Override
    public Object[] replacements() {
        InviteData data = (InviteData) getOptionals()[1];

        return List.of(
            "%player_name%", data.getPlayer().getName(),
            "%timestamp%", TextUtility.formatExtendedElapsedTime(data.getTimestamp())
        ).toArray();
    }

    @Override
    public void onClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        JoinRequestManageUI.promptPlayer(player, DATA);
    }


}
