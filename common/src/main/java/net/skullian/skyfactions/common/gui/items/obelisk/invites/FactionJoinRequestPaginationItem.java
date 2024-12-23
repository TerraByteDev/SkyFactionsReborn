package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.invites.JoinRequestManageUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;
import net.skullian.skyfactions.common.util.text.TextUtility;

import java.util.List;

public class FactionJoinRequestPaginationItem extends SkyItem {

    private final InviteData DATA;

    public FactionJoinRequestPaginationItem(ItemData data, SkyItemStack stack, SkyUser player, InviteData inviteData) {
        super(data, stack, player, List.of(inviteData).toArray());

        this.DATA = inviteData;
    }

    @Override
    public Object[] replacements() {
        InviteData data = (InviteData) getOPTIONALS()[1];

        return List.of(
            "player_name", data.getPlayer().getName(),
            "timestamp", TextUtility.formatExtendedElapsedTime(data.getTimestamp())
        ).toArray();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        JoinRequestManageUI.promptPlayer(player, DATA);
    }


}
