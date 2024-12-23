package net.skullian.skyfactions.common.gui.items.obelisk.invites;

import net.skullian.skyfactions.common.api.InvitesAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.gui.data.ItemData;
import net.skullian.skyfactions.common.gui.data.SkyClickType;
import net.skullian.skyfactions.common.gui.items.impl.SkyItem;
import net.skullian.skyfactions.common.gui.screens.obelisk.invites.JoinRequestsUI;
import net.skullian.skyfactions.common.gui.screens.obelisk.invites.PlayerOutgoingRequestManageUI;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.ErrorUtil;
import net.skullian.skyfactions.common.util.SkyItemStack;

public class JoinRequestsTypeItem extends SkyItem {

    private final String TYPE;

    public JoinRequestsTypeItem(ItemData data, SkyItemStack stack, String type, SkyUser player) {
        super(data, stack, player, null);
        
        this.TYPE = type;
    }

    @Override
    public void onClick(SkyClickType clickType, SkyUser player) {
        if (TYPE.equals("faction")) {
            JoinRequestsUI.promptPlayer(player);
        } else if (TYPE.equals("player")) {
            InvitesAPI.getPlayerJoinRequest(player.getUniqueId()).whenComplete(((joinRequestData, throwable) -> {
                if (throwable != null) {
                    ErrorUtil.handleError(player, "get your outgoing join request", "SQL_JOIN_REQUEST_GET", throwable);
                } else if (joinRequestData == null) {
                    SkyApi.getInstance().getSoundAPI().playSound(player, Settings.ERROR_SOUND.getString(), Settings.ERROR_SOUND_PITCH.getInt(), 1);
                    Messages.FACTION_JOIN_REQUEST_NOT_EXIST.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
                } else {
                    PlayerOutgoingRequestManageUI.promptPlayer(player, joinRequestData);
                }
            }));
        }
    }

}
