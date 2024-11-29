package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.invites.OutgoingInvitesUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class OutgoingInvitesTypeItem extends SkyItem {

    public OutgoingInvitesTypeItem(ItemData data, SkyItemStack stack, SkyUser player) {
        super(data, stack, player, null);
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        OutgoingInvitesUI.promptPlayer(player);
    }


}
