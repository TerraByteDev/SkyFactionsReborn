package net.skullian.skyfactions.database.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    public final Map<UUID, CacheEntry> playersToCache = new HashMap<>();
    public final Map<String, CacheEntry> factionsToCache = new HashMap<>();

    private BukkitTask task;

    public CompletableFuture<Void> cacheOnce() {
        return CompletableFuture.runAsync(() -> {
            SLogger.warn("Periodic Save - Running.");

            for (Map.Entry<UUID, CacheEntry> cachedPlayer : playersToCache.entrySet()) {
                cachedPlayer.getValue().cache(cachedPlayer.getKey().toString(), null).join();

                UUID uuid = cachedPlayer.getKey();
                int gemsModification = cachedPlayer.getValue().getGems();
                int runesModification = cachedPlayer.getValue().getRunes();

                playersToCache.remove(cachedPlayer.getKey());
                GemsAPI.playerGems.replace(uuid, (Math.max(0, GemsAPI.playerGems.get(uuid) + gemsModification)));
                RunesAPI.playerRunes.replace(uuid, (Math.max(0, RunesAPI.playerRunes.get(uuid) + runesModification)));
            }

            for (Map.Entry<String, CacheEntry> cachedFaction : factionsToCache.entrySet()) {
                cachedFaction.getValue().cache(null, cachedFaction.getKey()).join();

                String factionName = cachedFaction.getKey();
                Faction faction = FactionAPI.getCachedFaction(factionName);

                int gemsModification = cachedFaction.getValue().getGems();
                int runesModification = cachedFaction.getValue().getRunes();

                factionsToCache.remove(cachedFaction.getKey());
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

    public void addRunes(UUID playerUUID, int runes) {
        CacheEntry entry = playersToCache.computeIfAbsent(playerUUID, k -> new CacheEntry());
        entry.addRunes(runes);
    }

    public void addRunes(Faction faction, int runes) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
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
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.addGems(gems);
    }

    public void subtractGems(UUID playerUUID, int gems) {
        CacheEntry entry = playersToCache.computeIfAbsent(playerUUID, k -> new CacheEntry());
        entry.removeGems(gems);
    }

    public void subtractGems(Faction faction, int gems) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.removeGems(gems);
    }

    public void registerDefence(Faction faction, Location location) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.addDefence(location);
    }

    public void registerDefence(UUID playerUUID, Location location) {
        CacheEntry entry = playersToCache.computeIfAbsent(playerUUID, k -> new CacheEntry());
        entry.addDefence(location);
    }

    public void removeDefence(Faction faction, Location location) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.removeDefence(location);
    }

    public void removeDefence(UUID playerUUID, Location location) {
        CacheEntry entry = playersToCache.computeIfAbsent(playerUUID, k -> new CacheEntry());
        entry.removeDefence(location);
    }

    public void updateLocale(UUID playerUUID, String locale) {
        CacheEntry entry = playersToCache.computeIfAbsent(playerUUID, k -> new CacheEntry());
        entry.setLocale(locale);
    }

    public void updateLocale(Faction faction, String locale) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.setLocale(locale);
    }

    public void updatePlayerRank(Faction faction, UUID playerUUID, RankType newRank) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.setNewRank(playerUUID, newRank);
    }

    public void addFactionMember(Faction faction, OfflinePlayer player) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.addMember(player);
    }

    public void removeFactionMember(Faction faction, OfflinePlayer player) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.removeMember(player);
    }

    public void banFactionMember(Faction faction, OfflinePlayer player) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.banMember(player);
    }

    public void unbanFactionMember(Faction faction, OfflinePlayer player) {
        CacheEntry entry = factionsToCache.computeIfAbsent(faction.getName(), k -> new CacheEntry());
        entry.unbanMember(player);
    }
}
