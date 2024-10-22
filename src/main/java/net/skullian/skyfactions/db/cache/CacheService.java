package net.skullian.skyfactions.db.cache;

import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.faction.Faction;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CacheService {

    public final Map<UUID, CacheEntry> playersToCache = new HashMap<>();
    public final Map<Faction, CacheEntry> factionsToCache = new HashMap<>();

    private BukkitTask task;

    public void cacheOnce() {
        SLogger.warn("Periodic Save - Running.");

        for (Map.Entry<UUID, CacheEntry> cachedPlayer : playersToCache.entrySet()) {
            cachedPlayer.getValue().cache(cachedPlayer.getKey().toString(), null);

            playersToCache.remove(cachedPlayer.getKey());
        }

        for (Map.Entry<Faction, CacheEntry> cachedFaction : factionsToCache.entrySet()) {
            cachedFaction.getValue().cache(null, cachedFaction.getKey());

            factionsToCache.remove(cachedFaction.getKey());
        }

        SLogger.info("Periodic Save - Done.");
    }

    public void enable() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(SkyFactionsReborn.getInstance(), this::cacheOnce, 0L, 6000);
    }

    public void disable() {
        SLogger.info("Disabling Cache Service...");
        if (task != null) {
            task.cancel();
        }

        cacheOnce();
        SLogger.info("Cache Service Disabled!");
    }

    public void addRunes(UUID playerUUID, int runes) {
        CacheEntry entry = playersToCache.computeIfAbsent(playerUUID, k -> new CacheEntry());
        entry.addRunes(runes);
    }

    public void addRunes(Faction faction, int runes) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction, k -> new CacheEntry());
        entry.addRunes(runes);
    }

    public void subtractRunes(UUID playerUUID, int runes) {
        CacheEntry entry = playersToCache.get(playerUUID);
        if (entry != null) {
            entry.removeRunes(runes);
        }
    }

    public void subtractRunes(Faction faction, int runes) {
        CacheEntry entry = factionsToCache.get(faction);
        if (entry != null) {
            entry.removeRunes(runes);
        }
    }

    public void addGems(UUID playerUUID, int gems) {
        CacheEntry entry = playersToCache.computeIfAbsent(playerUUID, k -> new CacheEntry());
        entry.addGems(gems);
    }

    public void addGems(Faction faction, int gems) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction, k -> new CacheEntry());
        entry.addGems(gems);
    }

    public void subtractGems(UUID playerUUID, int gems) {
        CacheEntry entry = playersToCache.get(playerUUID);
        if (entry != null) {
            entry.removeGems(gems);
        }
    }

    public void subtractGems(Faction faction, int gems) {
        CacheEntry entry = factionsToCache.get(faction);
        if (entry != null) {
            entry.removeGems(gems);
        }
    }

}
