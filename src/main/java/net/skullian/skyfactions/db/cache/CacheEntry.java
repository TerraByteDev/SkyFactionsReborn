package net.skullian.skyfactions.db.cache;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.GemsAPI;
import net.skullian.skyfactions.api.RunesAPI;
import net.skullian.skyfactions.faction.Faction;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class CacheEntry {

    private int runes;
    private int gems;

    public void addRunes(int amount) {
        runes += amount;
    }

    public void removeRunes(int amount) {
        runes -= amount;
    }

    public void addGems(int amount) {
        gems += amount;
    }

    public void removeGems(int amount) {
        gems -= amount;
    }

    /**
     *
     * @param toCache - UUID
     * @param faction - explanatory
     * @return {@link CompletableFuture<Void>}
     */
    public CompletableFuture<Void> cache(String toCache, Faction faction) {
        if (!SkyFactionsReborn.databaseHandler.isActive()) {
            return CompletableFuture.completedFuture(null);
        }
        if (faction != null) {
            return CompletableFuture.allOf(
                    SkyFactionsReborn.databaseHandler.addGems(toCache, gems).exceptionally((ex) -> {
                        faction.gems -= gems;
                        if (faction.gems < 0) faction.gems = 0;

                        throw new RuntimeException("Failed to set gems of faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseHandler.addRunes(toCache, runes).exceptionally((ex) -> {
                        faction.runes -= runes;
                        if (faction.runes < 0) faction.runes = 0;

                        throw new RuntimeException("Failed to set runes of faction " + faction.getName(), ex);
                    })
            );
        } else {
            UUID uuid = UUID.fromString(toCache);
            return CompletableFuture.allOf(
                    SkyFactionsReborn.databaseHandler.addGems(uuid, gems).exceptionally((ex) -> {
                        GemsAPI.playerGems.replace(uuid, Math.max(0, GemsAPI.playerGems.get(uuid) - gems));

                        throw new RuntimeException("Failed to set gems of player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseHandler.addRunes(uuid, runes).exceptionally((ex) -> {
                        RunesAPI.playerRunes.replace(uuid, Math.max(0, RunesAPI.playerRunes.get(uuid) - runes));

                        throw new RuntimeException("Failed to set runes of player " + uuid, ex);
                    })
            );
        }
    }
}
