package net.skullian.skyfactions.core.api;

import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class InvitesAPI {

    public static final Map<UUID, List<InviteData>> playerIncomingInvites = new ConcurrentHashMap<>();
    public static final Map<UUID, JoinRequestData> playerJoinRequests = new ConcurrentHashMap<>();

    public static CompletableFuture<List<InviteData>> getPlayerIncomingInvites(UUID playerUUID) {
        if (playerIncomingInvites.containsKey(playerUUID)) return CompletableFuture.completedFuture(playerIncomingInvites.get(playerUUID));

        return SkyFactionsReborn.getDatabaseManager().getFactionInvitesManager().getInvitesOfPlayer(Bukkit.getOfflinePlayer(playerUUID)).whenComplete((data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else if (data != null) {
                playerIncomingInvites.put(playerUUID, data);
            }
        });
    }

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
