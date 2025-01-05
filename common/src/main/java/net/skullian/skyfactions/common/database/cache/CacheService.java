package net.skullian.skyfactions.common.database.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import lombok.Getter;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.util.SLogger;

public class CacheService {

    @Getter private final Map<UUID, CacheEntry> playersToCache = new ConcurrentHashMap<>();
    @Getter private final Map<String, CacheEntry> factionsToCache = new ConcurrentHashMap<>();
    private final Map<String, String> toRename = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> task;

    public CompletableFuture<Void> cacheOnce() {
        return CompletableFuture.runAsync(() -> {
            SLogger.warn("Periodic Save - Running.");

            for (Map.Entry<UUID, CacheEntry> cachedPlayer : playersToCache.entrySet()) {
                cachedPlayer.getValue().cache(cachedPlayer.getKey().toString(), null).join();

                UUID uuid = cachedPlayer.getKey();
                int gemsModification = cachedPlayer.getValue().getGems();
                int runesModification = cachedPlayer.getValue().getRunes();

                playersToCache.remove(uuid);

                SkyApi.getInstance().getUserManager().getUser(uuid).onCacheComplete(gemsModification, runesModification);
            }

            for (Map.Entry<String, CacheEntry> cachedFaction : factionsToCache.entrySet()) {
                cachedFaction.getValue().cache(null, cachedFaction.getKey()).join();

                String factionName = cachedFaction.getKey();
                Faction faction = SkyApi.getInstance().getFactionAPI().getCachedFaction(factionName);
                if (faction == null) continue;

                int gemsModification = cachedFaction.getValue().getGems();
                int runesModification = cachedFaction.getValue().getRunes();
                factionsToCache.remove(factionName);

                faction.gems += gemsModification;
                faction.runes += runesModification;

                if (faction.gems < 0) faction.gems = 0;
                if (faction.runes < 0) faction.runes = 0;
            }

            SLogger.info("Periodic Save - Done.");
        }, executorService);
    }

    public void enable() {
        this.task = executorService.scheduleAtFixedRate(() -> cacheOnce().thenRun(this::onCacheComplete), Settings.CACHE_SAVE_INTERVAL.getInt(), Settings.CACHE_SAVE_INTERVAL.getInt(), TimeUnit.SECONDS);
    }

    private void onCacheComplete() {
        for (Map.Entry<String, String> rename : toRename.entrySet()) {
            CacheEntry entry = factionsToCache.remove(rename.getKey());
            factionsToCache.put(rename.getValue(), entry);

            toRename.remove(rename.getKey());
        }
    }

    public CompletableFuture<Void> disable() {
        SLogger.info("Disabling Cache Service...");
        if (task != null && !task.isCancelled()) {
            task.cancel(false);
        }

        return cacheOnce().orTimeout(60, TimeUnit.SECONDS);
    }

    public CacheEntry getEntry(UUID playerUUID) {
        return playersToCache.computeIfAbsent(playerUUID, k -> new CacheEntry());
    }

    public CacheEntry getEntry(String factionName) {
        return factionsToCache.computeIfAbsent(factionName, k -> new CacheEntry());
    }

    public CacheEntry getEntry(Faction faction) {
        return factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
    }

    public void onFactionRename(String oldName, String newName) {
        toRename.put(oldName, newName);
    }
}
