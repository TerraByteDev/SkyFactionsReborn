package net.skullian.skyfactions.database.cache;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import net.skullian.skyfactions.faction.RankType;
import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.faction.Faction;
import org.bukkit.OfflinePlayer;

@Getter
public class CacheEntry {

    private int runes = 0; // Player & Faction
    private int gems = 0; // Player & Faction
    private final List<Location> defencesToRegister = new ArrayList<>(); // Player * Faction
    private final List<Location> defencesToRemove = new ArrayList<>(); // Player & Faction
    private String newLocale; // Player & Faction

    private final Map<UUID, RankType> newRanks = new HashMap<>(); // Faction Exclusive
    private final List<OfflinePlayer> membersToAdd = new ArrayList<>(); // Faction Exclusive
    private final List<OfflinePlayer> membersToRemove = new ArrayList<>(); // Faction Exclusive
    private final List<OfflinePlayer> membersToBan = new ArrayList<>(); // Faction Exclusive
    private final List<OfflinePlayer> membersToUnban = new ArrayList<>();

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

    public void setNewRank(UUID playerUUID, RankType rankType) {
        newRanks.put(playerUUID, rankType);
    }

    public void addMember(OfflinePlayer player) {
        membersToRemove.remove(player);
        membersToAdd.add(player);
    }

    public void removeMember(OfflinePlayer player) {
        membersToAdd.remove(player);
        membersToRemove.add(player);
    }

    public void banMember(OfflinePlayer player) {
        membersToUnban.remove(player);
        membersToBan.add(player);
    }

    public void unbanMember(OfflinePlayer player) {
        membersToBan.remove(player);
        membersToBan.add(player);
    }

    /**
     *
     * @param toCache - UUID
     * @param faction - explanatory
     * @return {@link CompletableFuture<Void>}
     */
    public CompletableFuture<Void> cache(String toCache, Faction faction) {
        if (SkyFactionsReborn.databaseManager.closed) {
            return CompletableFuture.completedFuture(null);
        }
        if (faction != null) {
            return CompletableFuture.allOf(
                    SkyFactionsReborn.databaseManager.currencyManager.modifyGems(faction.getName(), gems, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set gems of faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.currencyManager.modifyRunes(faction.getName(), runes, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set runes of faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.defencesManager.registerDefenceLocations(defencesToRegister, faction.getName(), true).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register new defences for faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.defencesManager.removeDefenceLocations(defencesToRemove, faction.getName(), true).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove defences for faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.factionsManager.updateFactionLocale(faction.getName(), newLocale).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update locale for faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.factionsManager.updateFactionMemberRanks(faction.getName(), newRanks).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update ranks for faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.factionsManager.addFactionMembers(membersToAdd, faction.getName()).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to add players to faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.factionsManager.removeMembers(membersToRemove, faction.getName()).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to kick players from faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.factionsManager.banMembers(membersToBan, faction.getName()).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to ban players from faction " + faction.getName(), ex);
                    }),
                    SkyFactionsReborn.databaseManager.factionsManager.unbanMembers(membersToUnban, faction.getName()).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to unban players from faction " + faction.getName(), ex);
                    })
            );
        } else {
            UUID uuid = UUID.fromString(toCache);
            return CompletableFuture.allOf(
                    SkyFactionsReborn.databaseManager.currencyManager.modifyGems(uuid.toString(), gems, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set gems of player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseManager.currencyManager.modifyRunes(uuid.toString(), runes, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set runes of player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseManager.defencesManager.registerDefenceLocations(defencesToRegister, uuid.toString(), false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register new defences for player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseManager.defencesManager.registerDefenceLocations(defencesToRemove, uuid.toString(), false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove defences for player " + uuid, ex);
                    }),
                    SkyFactionsReborn.databaseManager.playerManager.setPlayerLocale(uuid, newLocale).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update defendes for player " + uuid, ex);
                    })
            );
        }
    }
}
