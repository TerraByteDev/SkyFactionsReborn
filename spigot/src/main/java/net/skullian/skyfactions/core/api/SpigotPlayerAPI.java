package net.skullian.skyfactions.core.api;

import net.skullian.skyfactions.common.api.PlayerAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.PlayerData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotPlayerAPI extends PlayerAPI {

    // Player Data //

    public static final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Boolean> isPlayerRegistered(UUID uuid) {
        if (playerData.containsKey(uuid)) return CompletableFuture.completedFuture(true);

        return getPlayerData(uuid).handle((data, ex) -> data != null);
    }

    @Override
    public void cacheData(UUID playerUUID) {
        if (!SpigotGemsAPI.playerGems.containsKey(playerUUID)) SpigotGemsAPI.cachePlayer(playerUUID);
        if (!SkyApi.getInstance().getRunesAPI().isPlayerCached(playerUUID)) SkyApi.getInstance().getRunesAPI().cachePlayer(playerUUID);
        if (!SpigotFactionAPI.factionCache.containsKey(playerUUID)) SpigotFactionAPI.getFaction(playerUUID);
        if (!SpigotInvitesAPI.playerIncomingInvites.containsKey(playerUUID) || !SpigotInvitesAPI.playerJoinRequests.containsKey(playerUUID)) SpigotInvitesAPI.cache(playerUUID);
    }

    @Override
    public boolean isPlayerCached(UUID uuid) {
        return playerData.containsKey(uuid);
    }

    @Override
    public CompletableFuture<PlayerData> getPlayerData(UUID uuid) {
        if (playerData.containsKey(uuid)) return CompletableFuture.completedFuture(playerData.get(uuid));

        return SkyApi.getInstance().getDatabaseManager().getPlayerManager().getPlayerData(uuid).handle((data, ex) -> {
            if (data != null) {
                playerData.put(uuid, data);
            }
            return data;
        });
    }

    @NotNull
    @Override
    public PlayerData getCachedPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    @Override
    public String getLocale(UUID uuid) {
        return playerData.getOrDefault(uuid, getDefaultPlayerData()).getLOCALE();
    }
}
