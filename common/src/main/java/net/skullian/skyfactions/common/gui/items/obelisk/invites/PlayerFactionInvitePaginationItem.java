package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.invites.PlayerManageIncomingInviteUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

import java.util.List;

public class PlayerFactionInvitePaginationItem extends SkyItem {

    private final InviteData DATA;

    public PlayerFactionInvitePaginationItem(ItemData data, SkyItemStack stack, SkyUser player, InviteData inviteData) {
        super(data, stack, player, List.of(inviteData).toArray());

        this.DATA = inviteData;
    }

    @Override
    public Object[] replacements() {
        InviteData data = (InviteData) getOPTIONALS()[0];

        return List.of(
            "faction_name", data.getFactionName(),
            "player_name", data.getInviter().getName()
        ).toArray();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        PlayerManageIncomingInviteUI.promptPlayer(player, DATA);
    }

}
