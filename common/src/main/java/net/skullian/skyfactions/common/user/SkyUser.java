package net.skullian.skyfactions.common.user;

import lombok.Getter;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.notification.NotificationData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public abstract class SkyUser {

    public abstract UUID getUniqueId();

    public abstract void teleport(Object location);

    public abstract PlayerData getPlayerData();

    public abstract CompletableFuture<Integer> getGems();

    public abstract CompletableFuture<Integer> getRunes();

    @Nullable public abstract CompletableFuture<PlayerIsland> getIsland();

    public abstract CompletableFuture<String> getBelongingFaction();

    public abstract List<NotificationData> getNotifications();

    public abstract CompletableFuture<List<InviteData>> getIncomingInvites();

    public abstract List<InviteData> getCachedInvites();

    public abstract void onCacheComplete(int runesAddition, int gemsAddition);

    @Nullable public abstract CompletableFuture<JoinRequestData> getActiveJoinRequest();

}
