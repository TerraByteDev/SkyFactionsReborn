package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.faction.JoinRequestData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class InvitesAPI {

    public static CompletableFuture<List<InviteData>> getPlayerIncomingInvites(UUID playerUUID) {
        return SkyApi.getInstance().getUserManager().getUser(playerUUID).getIncomingInvites();
    }

    public static CompletableFuture<JoinRequestData> getPlayerJoinRequest(UUID playerUUID) {
        return SkyApi.getInstance().getUserManager().getUser(playerUUID).getActiveJoinRequest();
    }

    public static void onInviteCreate(UUID playerUUID, InviteData data) {
        SkyApi.getInstance().getUserManager().getUser(playerUUID).getCachedInvites().add(data);
    }

    public static void onInviteRemove(UUID playerUUID, InviteData data) {
        SkyApi.getInstance().getUserManager().getUser(playerUUID).getCachedInvites().remove(data);
    }

}
