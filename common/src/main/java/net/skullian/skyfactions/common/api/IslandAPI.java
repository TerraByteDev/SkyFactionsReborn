package net.skullian.skyfactions.common.api;

import net.skullian.skyfactions.common.island.impl.PlayerIsland;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class IslandAPI {

    private final Map<UUID, PlayerIsland> islands;
    private final List<UUID> awaitingDeletion;

    public IslandAPI() {
        this.islands = new ConcurrentHashMap<>();
        this.awaitingDeletion = new ArrayList<>();
    }

    public CompletableFuture<PlayerIsland> getPlayerIsland(UUID playerUUID) {
        if (islands.containsKey(playerUUID)) return CompletableFuture.completedFuture(islands.get(playerUUID));
        return SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().getPlayerIsland(playerUUID).thenApply((island) -> {
            islands.put(playerUUID, island);

            return island;
        });
    }


}
