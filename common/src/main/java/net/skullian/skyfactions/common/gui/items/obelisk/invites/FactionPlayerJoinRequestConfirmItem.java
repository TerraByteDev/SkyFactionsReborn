package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class FactionPlayerJoinRequestConfirmItem extends SkyItem {
    private JoinRequestData DATA;

    public FactionPlayerJoinRequestConfirmItem(ItemData data, SkyItemStack stack, JoinRequestData joinRequestData, SkyUser player) {
        super(data, stack, player, null);
        
        this.DATA = joinRequestData;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        player.closeInventory();

        SkyApi.getInstance().getFactionAPI().getFaction(DATA.getFactionName()).whenComplete(((faction, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", throwable);
                return;
            } else if (faction == null) {
                Messages.ERROR.send(player, locale, "operation", "get your Faction", "FACTION_NOT_FOUND");
                return;
            }

            SkyApi.getInstance().getCacheService().getEntry(player.getUniqueId()).removeInvite(faction.toInviteData(DATA, player));
            faction.addFactionMember(player);
            Messages.PLAYER_FACTION_JOIN_SUCCESS.send(player, locale, "faction_name", DATA.getFactionName());
            SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().replace(faction.getName(), (SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().get(faction.getName()) - 1));
        }));
    }

}
