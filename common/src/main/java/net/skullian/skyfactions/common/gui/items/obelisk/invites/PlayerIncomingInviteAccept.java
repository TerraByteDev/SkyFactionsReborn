package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.faction.AuditLogType;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class PlayerIncomingInviteAccept extends SkyItem {

    private InviteData DATA;

    public PlayerIncomingInviteAccept(ItemData data, SkyItemStack stack, InviteData inviteData, SkyUser player) {
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
            } else if (faction != null) {
                Messages.ALREADY_IN_FACTION.send(player, locale);
                return;
            }

            SkyApi.getInstance().getFactionAPI().getFaction(DATA.getFactionName()).whenComplete((dataFaction, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "get the Faction", "SQL_FACTION_GET", throwable);
                    return;
                }

                dataFaction.revokeInvite(DATA, AuditLogType.INVITE_ACCEPT, "player_name", player.getName());
                dataFaction.addFactionMember(player);

                Messages.PLAYER_FACTION_JOIN_SUCCESS.send(player, locale, "faction_name", dataFaction.getName());
            });
        });
    }
}
