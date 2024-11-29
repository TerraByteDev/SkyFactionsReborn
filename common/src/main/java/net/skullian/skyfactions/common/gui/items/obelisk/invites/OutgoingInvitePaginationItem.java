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
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OutgoingInvitePaginationItem extends SkyItem {

    private InviteData DATA;

    public OutgoingInvitePaginationItem(ItemData data, SkyItemStack stack, InviteData inviteData, SkyUser player) {
        super(data, stack, player, List.of(inviteData).toArray());
        
        this.DATA = inviteData;
    }

    @Override
    public Object[] replacements() {
        InviteData data = (InviteData) getOPTIONALS()[0];

        return List.of(
            "inviter", data.getInviter().getName(),
            "player_name", data.getPlayer().getName(),
            "timestamp", TextUtility.formatExtendedElapsedTime(data.getTimestamp())
        ).toArray();
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
        if (clickType.isRightClick()) {
            player.closeInventory();

            SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "get your Faction", "SQL_FACTION_GET", throwable);
                    return;
                } else if (faction == null) {
                    Messages.ERROR.send(player, locale, "operation", "get your Faction", "FACTION_NOT_FOUND");
                    return;
                }

                faction.revokeInvite(DATA, AuditLogType.INVITE_REVOKE, "player", player.getName(), "invited", DATA.getPlayer().getName());
                Messages.FACTION_INVITE_REVOKE_SUCCESS.send(player, locale, "player_name", DATA.getPlayer().getName());
            });
        }
    }

}
