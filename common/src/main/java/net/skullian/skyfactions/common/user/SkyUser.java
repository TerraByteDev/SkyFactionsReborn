package net.skullian.skyfactions.common.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.island.SkyIsland;
import net.skullian.skyfactions.common.notification.NotificationData;

import java.util.List;

@Getter
public abstract class SkyUser {

    public abstract PlayerData getPlayerData();

    public abstract Object getPlayerInstance();

    public abstract int getGems();

    public abstract int getRunes();

    public abstract SkyIsland getIsland();

    public abstract String getBelongingFaction();

    public abstract List<NotificationData> getNotifications();

    public abstract List<InviteData> getIncomingInvites();

    public abstract JoinRequestData getActiveJoinRequest();

}
