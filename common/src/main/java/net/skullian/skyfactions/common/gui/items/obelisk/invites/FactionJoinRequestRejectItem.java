package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class FactionJoinRequestRejectItem extends SkyItem {

    private InviteData DATA;

    public FactionJoinRequestRejectItem(ItemData data, SkyItemStack stack, InviteData inviteData, SkyUser player) {
        super(data, stack, player, null);
        
        this.DATA = inviteData;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        player.closeInventory();

        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", ex);
                return;
            } else if (faction == null) {
                Messages.ERROR.send(player, locale, "operation", "get your Faction", "FACTION_NOT_FOUND");
                return;
            }

            faction.rejectJoinRequest(DATA, player);
            Messages.FACTION_JOIN_REQUEST_REJECT_SUCCESS.send(player, locale, "player_name", DATA.getPlayer().getName());
        });
    }

}