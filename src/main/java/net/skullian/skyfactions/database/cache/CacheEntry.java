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
import org.jetbrains.annotations.Nullable;

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
     * @param toCache - UUID of player to cache (only used when the entry is for a player)
     * @param factionName - explanatory. same reason as above.
     * @return {@link CompletableFuture<Void>}
     */
    public CompletableFuture<Void> cache(@Nullable String toCache, @Nullable String factionName) {
        if (SkyFactionsReborn.getDatabaseManager().closed) {
            return CompletableFuture.completedFuture(null);
        }
        if (factionName != null) {
            return CompletableFuture.allOf(
                    SkyFactionsReborn.getDatabaseManager().getCurrencyManager().modifyGems(factionName, gems, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set gems of faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getCurrencyManager().modifyRunes(factionName, runes, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set runes of faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getDefencesManager().registerDefenceLocations(defencesToRegister, factionName, true).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register new defences for faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getDefencesManager().removeDefenceLocations(defencesToRemove, factionName, true).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove defences for faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getFactionsManager().updateFactionLocale(factionName, newLocale).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update locale for faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getFactionsManager().updateFactionMemberRanks(factionName, newRanks).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update ranks for faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getFactionsManager().addFactionMembers(membersToAdd, factionName).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to add players to faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getFactionsManager().removeMembers(membersToRemove, factionName).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to kick players from faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getFactionsManager().banMembers(membersToBan, factionName).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to ban players from faction " + factionName, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getFactionsManager().unbanMembers(membersToUnban, factionName).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to unban players from faction " + factionName, ex);
                    })
            );
        } else {
            UUID uuid = UUID.fromString(toCache);
            return CompletableFuture.allOf(
                    SkyFactionsReborn.getDatabaseManager().getCurrencyManager().modifyGems(uuid.toString(), gems, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set gems of player " + uuid, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getCurrencyManager().modifyRunes(uuid.toString(), runes, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set runes of player " + uuid, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getDefencesManager().registerDefenceLocations(defencesToRegister, uuid.toString(), false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register new defences for player " + uuid, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getDefencesManager().registerDefenceLocations(defencesToRemove, uuid.toString(), false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove defences for player " + uuid, ex);
                    }),
                    SkyFactionsReborn.getDatabaseManager().getPlayerManager().setPlayerLocale(uuid, newLocale).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update defendes for player " + uuid, ex);
                    })
            );
        }
    }
}
