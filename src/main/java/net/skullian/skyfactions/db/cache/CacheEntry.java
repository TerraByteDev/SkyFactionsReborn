package net.skullian.skyfactions.db.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.faction.Faction;

@Getter
@Setter
public class CacheEntry {

    private int runes = 0;
    private int gems = 0;
    private List<Location> defencesToRegister = new ArrayList<>();
    private List<Location> defencesToRemove = new ArrayList<>();
    private String newLocale;

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

    public void addDefence(Location location) {
        defencesToRemove.remove(location);
        defencesToRegister.add(location);
    }

    public void removeDefence(Location location) {
        defencesToRegister.remove(location);
        defencesToRemove.add(location);
    }

    public void setLocale(String locale) {
        newLocale = locale;
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
                        throw new RuntimeException("Failed to set gems of faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseHandler.addRunes(toCache, runes).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set runes of faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseHandler.registerDefenceLocations(toCache, defencesToRegister).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register new defences for faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseHandler.removeDefences(toCache, defencesToRemove).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove defences for faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseHandler.setFactionLocale(toCache, newLocale).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update locale for faction " + faction.getName(), ex);
                    })
            );
        } else {
            UUID uuid = UUID.fromString(toCache);
            return CompletableFuture.allOf(
                    SkyFactionsReborn.databaseHandler.addGems(uuid, gems).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set gems of player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseHandler.addRunes(uuid, runes).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set runes of player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseHandler.registerDefenceLocations(uuid, defencesToRegister).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register new defences for player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseHandler.removeDefences(uuid, defencesToRemove).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove defences for player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseHandler.setLocale(uuid, newLocale).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update defendes for player " + uuid, ex);
                    })
            );
        }
    }
}
