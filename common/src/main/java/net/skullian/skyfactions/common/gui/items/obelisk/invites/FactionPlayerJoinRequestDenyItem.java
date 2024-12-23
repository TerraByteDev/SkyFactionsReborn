package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class FactionPlayerJoinRequestDenyItem extends SkyItem {

    private final JoinRequestData DATA;

    public FactionPlayerJoinRequestDenyItem(ItemData data, SkyItemStack stack, JoinRequestData joinRequestData, SkyUser player) {
        super(data, stack, player, null);
        
        this.DATA = joinRequestData;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        player.closeInventory();

        SkyApi.getInstance().getFactionAPI().getFaction(DATA.getFactionName()).whenComplete((faction, throwable) -> {
            if (throwable != null) {
                ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", throwable);
                return;
            } else if (faction == null) {
                Messages.ERROR.send(player, locale, "operation", "get your Faction", "FACTION_NOT_FOUND");
                return;
            }

            faction.revokeInvite(faction.toInviteData(DATA, player), AuditLogType.PLAYER_JOIN, "player_name", player.getName());

            Messages.FACTION_JOIN_REQUEST_DENY_SUCCESS.send(player, locale, "faction_name", DATA.getFactionName());
            SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().replace(faction.getName(), (SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().get(faction.getName()) - 1));
        });
    }
}
