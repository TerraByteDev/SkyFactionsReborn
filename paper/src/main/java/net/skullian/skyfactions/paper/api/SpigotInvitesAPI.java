package net.skullian.skyfactions.paper.api;

import net.skullian.skyfactions.common.api.InvitesAPI;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpigotInvitesAPI extends InvitesAPI {

    public static CompletableFuture<JoinRequestData> getPlayerJoinRequest(UUID playerUUID) {
        if (playerJoinRequests.containsKey(playerUUID)) return CompletableFuture.completedFuture(playerJoinRequests.get(playerUUID));

       return  SkyFactionsReborn.getDatabaseManager().getFactionInvitesManager().getPlayerJoinRequest(Bukkit.getOfflinePlayer(playerUUID)).whenComplete((data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            } else if (data != null) {
                playerJoinRequests.put(playerUUID, data);
            }
        });
    }

    public static void onInviteCreate(UUID playerUUID, InviteData data) {
        getPlayerIncomingInvites(playerUUID).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            List<InviteData> invites = playerIncomingInvites.get(playerUUID);
            if (invites == null) return; // sanity

            invites.add(data);
        });
    }

    public static void onInviteRemove(UUID playerUUID, InviteData data) {
        getPlayerIncomingInvites(playerUUID).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            List<InviteData> invites = playerIncomingInvites.get(playerUUID);
            if (invites == null) return; // sanity

            invites.remove(data);
        });
    }

    public static void cache(UUID playerUUID) {
        getPlayerIncomingInvites(playerUUID);
        getPlayerJoinRequest(playerUUID);
    }
}
