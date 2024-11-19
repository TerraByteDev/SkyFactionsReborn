package net.skullian.skyfactions.database.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.faction.RankType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.util.SLogger;

public class CacheService {

    @Getter private final Map<UUID, CacheEntry> playersToCache = new HashMap<>();
    @Getter private final Map<String, CacheEntry> factionsToCache = new HashMap<>();

    private BukkitTask task;

    public CompletableFuture<Void> cacheOnce() {
        return CompletableFuture.runAsync(() -> {
            SLogger.warn("Periodic Save - Running.");

            for (Map.Entry<UUID, CacheEntry> cachedPlayer : playersToCache.entrySet()) {
                cachedPlayer.getValue().cache(cachedPlayer.getKey().toString(), null).join();

                UUID uuid = cachedPlayer.getKey();
                int gemsModification = cachedPlayer.getValue().getGems();
                int runesModification = cachedPlayer.getValue().getRunes();

                GemsAPI.playerGems.replace(uuid, (Math.max(0, GemsAPI.playerGems.get(uuid) + gemsModification)));
                RunesAPI.playerRunes.replace(uuid, (Math.max(0, RunesAPI.playerRunes.get(uuid) + runesModification)));
            }

            for (Map.Entry<String, CacheEntry> cachedFaction : factionsToCache.entrySet()) {
                cachedFaction.getValue().cache(null, cachedFaction.getKey()).join();

                String factionName = cachedFaction.getKey();
                Faction faction = FactionAPI.getCachedFaction(factionName);

                int gemsModification = cachedFaction.getValue().getGems();
                int runesModification = cachedFaction.getValue().getRunes();

                faction.gems += gemsModification;
                faction.runes += runesModification;

                if (faction.gems < 0) faction.gems = 0;
                if (faction.runes < 0) faction.runes = 0;
            }

            SLogger.info("Periodic Save - Done.");
        });
    }

    public void enable() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(SkyFactionsReborn.getInstance(), this::cacheOnce, Settings.CACHE_SAVE_INTERVAL.getInt() * 20L, Settings.CACHE_SAVE_INTERVAL.getInt() * 20L);
    }

    public CompletableFuture<Void> disable() {
        SLogger.info("Disabling Cache Service...");
        if (task != null) {
            task.cancel();
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
}
