package net.skullian.skyfactions.common.database.impl.faction;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.tables.records.FactionBansRecord;
import net.skullian.skyfactions.common.database.tables.records.FactionIslandsRecord;
import net.skullian.skyfactions.common.database.tables.records.FactionMembersRecord;
import net.skullian.skyfactions.common.database.tables.records.FactionsRecord;
import net.skullian.skyfactions.common.api.PlayerAPI;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.faction.RankType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.common.database.tables.DefenceLocations.DEFENCE_LOCATIONS;
import static net.skullian.skyfactions.common.database.tables.FactionBans.FACTION_BANS;
import static net.skullian.skyfactions.common.database.tables.FactionInvites.FACTION_INVITES;
import static net.skullian.skyfactions.common.database.tables.FactionIslands.FACTION_ISLANDS;
import static net.skullian.skyfactions.common.database.tables.FactionMembers.FACTION_MEMBERS;
import static net.skullian.skyfactions.common.database.tables.Factions.FACTIONS;
import static net.skullian.skyfactions.common.database.tables.AuditLogs.AUDIT_LOGS;
import static net.skullian.skyfactions.common.database.tables.FactionElections.FACTION_ELECTIONS;
import static net.skullian.skyfactions.common.database.tables.ElectionVotes.ELECTION_VOTES;

public class FactionsDatabaseManager {

    private final DSLContext ctx;

    public FactionsDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<Void> registerFaction(Player factionOwner, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(FACTIONS)
                    .columns(FACTIONS.NAME, FACTIONS.MOTD, FACTIONS.LEVEL, FACTIONS.LAST_RAID, FACTIONS.LOCALE, FACTIONS.LAST_RENAMED)
                    .values(factionName, "<red>None", 1, (long) 0, SkyApi.getInstance().getPlayerAPI().getLocale(factionOwner.getUniqueId()), System.currentTimeMillis())
                    .execute();

            ctx.insertInto(FACTION_MEMBERS)
                    .columns(FACTION_MEMBERS.FACTIONNAME, FACTION_MEMBERS.UUID, FACTION_MEMBERS.RANK)
                    .values(factionName, factionOwner.getUniqueId().toString(), "owner")
                    .execute();
        });
    }

    public CompletableFuture<Faction> getFaction(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            FactionsRecord result = ctx.selectFrom(FACTIONS)
                    .where(FACTIONS.NAME.eq(factionName))
                    .fetchOne();

            return result != null ?
                    new Faction(
                            SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().getFactionIsland(factionName).join(),
                            result.getName(),
                            result.getLastRaid(),
                            result.getLevel(),
                            getFactionOwner(factionName).join(),
                            getFactionMembersByRank(factionName, RankType.ADMIN).join(),
                            getFactionMembersByRank(factionName, RankType.MODERATOR).join(),
                            getFactionMembersByRank(factionName, RankType.ADMIN).join(),
                            getFactionMembersByRank(factionName, RankType.MEMBER).join(),
                            getFactionMOTD(factionName).join(),
                            SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getRunes(factionName).join(),
                            SkyApi.getInstance().getDatabaseManager().getCurrencyManager().getGems(factionName).join(),
                            result.getLocale(),
                            SkyApi.getInstance().getDatabaseManager().getElectionManager().isElectionRunning(factionName).join(),
                            getBannedPlayers(factionName).join(),
                            result.getLastRenamed(),
                            SkyApi.getInstance().getDatabaseManager().getFactionInvitesManager().getAllInvites(factionName).join(),
                            SkyApi.getInstance().getDatabaseManager().getFactionAuditLogManager().getAuditLogs(factionName).join()
                    ) : null;
        });
    }

    public CompletableFuture<Faction> getFaction(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            FactionMembersRecord result = ctx.selectFrom(FACTION_MEMBERS)
                    .where(FACTION_MEMBERS.UUID.eq(playerUUID.toString()))
                    .fetchOne();

            return result != null ? getFaction(result.getFactionname()).join() : null;
        });
    }

    public CompletableFuture<Faction> getFactionByIslandID(int id) {
        return CompletableFuture.supplyAsync(() -> {
            FactionIslandsRecord result = ctx.selectFrom(FACTION_ISLANDS)
                    .where(FACTION_ISLANDS.ID.eq(id))
                    .fetchOne();

            return result != null ? getFaction(result.getFactionname()).join() : null;
        });
    }

    public CompletableFuture<Void> updateFactionName(String oldName, String newName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                trx.dsl().update(FACTIONS)
                        .set(FACTIONS.NAME, newName)
                        .where(FACTIONS.NAME.eq(oldName))
                        .execute();

                trx.dsl().update(FACTION_BANS)
                        .set(FACTION_BANS.FACTIONNAME, newName)
                        .where(FACTION_BANS.FACTIONNAME.eq(oldName))
                        .execute();

                trx.dsl().update(FACTION_MEMBERS)
                        .set(FACTION_MEMBERS.FACTIONNAME, newName)
                        .where(FACTION_MEMBERS.FACTIONNAME.eq(oldName))
                        .execute();

                trx.dsl().update(AUDIT_LOGS)
                        .set(AUDIT_LOGS.FACTIONNAME, newName)
                        .where(AUDIT_LOGS.FACTIONNAME.eq(oldName))
                        .execute();

                trx.dsl().update(DEFENCE_LOCATIONS)
                        .set(DEFENCE_LOCATIONS.FACTIONNAME, newName)
                        .where(DEFENCE_LOCATIONS.FACTIONNAME.eq(oldName))
                        .execute();

                trx.dsl().update(FACTION_INVITES)
                        .set(FACTION_INVITES.FACTIONNAME, newName)
                        .where(FACTION_INVITES.FACTIONNAME.eq(oldName))
                        .execute();

                trx.dsl().update(FACTION_ISLANDS)
                        .set(FACTION_ISLANDS.FACTIONNAME, newName)
                        .where(FACTION_ISLANDS.FACTIONNAME.eq(oldName))
                        .execute();

                trx.dsl().update(FACTION_ELECTIONS)
                        .set(FACTION_ELECTIONS.FACTIONNAME, newName)
                        .where(FACTION_ELECTIONS.FACTIONNAME.eq(oldName))
                        .execute();
            });
        });
    }

    public CompletableFuture<String> getFactionMOTD(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.select(FACTIONS.MOTD)
                .from(FACTIONS)
                .where(FACTIONS.NAME.eq(factionName))
                .fetchOneInto(String.class));
    }

    public CompletableFuture<Void> updateFactionMOTD(String factionName, String newMOTD) {
        return CompletableFuture.runAsync(() -> {
            ctx.update(FACTIONS)
                    .set(FACTIONS.MOTD, newMOTD)
                    .where(FACTIONS.NAME.eq(factionName))
                    .execute();
        });
    }

    public CompletableFuture<Void> updateFactionLocale(String factionName, String newLocale) {
        return CompletableFuture.runAsync(() -> {
            if (newLocale == null) return;
            ctx.update(FACTIONS)
                    .set(FACTIONS.LOCALE, newLocale)
                    .where(FACTIONS.NAME.eq(factionName))
                    .execute();
        });
    }

    public CompletableFuture<Void> removeFaction(String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                trx.dsl().deleteFrom(FACTION_ISLANDS)
                        .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                        .execute();

                trx.dsl().deleteFrom(FACTION_MEMBERS)
                        .where(FACTION_MEMBERS.FACTIONNAME.eq(factionName))
                        .execute();

                trx.dsl().deleteFrom(FACTION_BANS)
                        .where(FACTION_BANS.FACTIONNAME.eq(factionName))
                        .execute();

                trx.dsl().deleteFrom(FACTION_INVITES)
                        .where(FACTION_INVITES.FACTIONNAME.eq(factionName))
                        .execute();

                trx.dsl().deleteFrom(AUDIT_LOGS)
                        .where(AUDIT_LOGS.FACTIONNAME.eq(factionName))
                        .execute();

                int id = SkyApi.getInstance().getDatabaseManager().getElectionManager().getElectionID(factionName).join();
                trx.dsl().deleteFrom(FACTION_ELECTIONS)
                        .where(FACTION_ELECTIONS.FACTIONNAME.eq(factionName))
                        .execute();

                trx.dsl().deleteFrom(ELECTION_VOTES)
                        .where(ELECTION_VOTES.ELECTION.eq(id))
                        .execute();
            });
        });
    }


    // ------------------ MEMBERS  ------------------ //

    public CompletableFuture<Void> addFactionMembers(List<OfflinePlayer> players, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (OfflinePlayer player : players) {
                    trx.dsl().insertInto(FACTION_MEMBERS)
                            .columns(FACTION_MEMBERS.FACTIONNAME, FACTION_MEMBERS.UUID, FACTION_MEMBERS.RANK)
                            .values(factionName, player.getUniqueId().toString(), "member")
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Void> updateFactionMemberRanks(String factionName, Map<UUID, RankType> ranks) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (Map.Entry<UUID, RankType> entry : ranks.entrySet()) {
                    trx.dsl().update(FACTION_MEMBERS)
                            .set(FACTION_MEMBERS.RANK, entry.getValue().getRankValue())
                            .where(FACTION_MEMBERS.UUID.eq(entry.getKey().toString()), FACTION_MEMBERS.FACTIONNAME.eq(factionName))
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Boolean> isInFaction(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(FACTION_MEMBERS, FACTION_MEMBERS.UUID.eq(playerUUID.toString())));
    }

    public CompletableFuture<OfflinePlayer> getFactionOwner(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            FactionMembersRecord result = ctx.selectFrom(FACTION_MEMBERS)
                    .where(FACTION_MEMBERS.FACTIONNAME.eq(factionName), FACTION_MEMBERS.RANK.eq("owner"))
                    .fetchOne();

            return result != null ? Bukkit.getOfflinePlayer(UUID.fromString(result.getUuid())) : null;
        });
    }

    public CompletableFuture<List<OfflinePlayer>> getFactionMembersByRank(String factionName, RankType rank) {
        return CompletableFuture.supplyAsync(() -> {
            Result<FactionMembersRecord> results = ctx.selectFrom(FACTION_MEMBERS)
                    .where(FACTION_MEMBERS.FACTIONNAME.eq(factionName), FACTION_MEMBERS.RANK.eq(rank.getRankValue()))
                    .fetch();

            List<OfflinePlayer> players = new ArrayList<>();
            for (FactionMembersRecord member : results) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(member.getUuid()));
                players.add(player);
            }

            return players;
        });
    }

    // ------------------ ADMINISTRATION  ------------------ //

    public CompletableFuture<Void> removeMembers(List<OfflinePlayer> players, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (OfflinePlayer player : players) {
                    trx.dsl().deleteFrom(FACTION_MEMBERS)
                            .where(FACTION_MEMBERS.UUID.eq(player.getUniqueId().toString()), FACTION_MEMBERS.FACTIONNAME.eq(factionName))
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Void> banMembers(List<OfflinePlayer> players, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                removeMembers(players, factionName).join();

                for (OfflinePlayer player : players) {
                    trx.dsl().insertInto(FACTION_BANS)
                            .columns(FACTION_BANS.FACTIONNAME, FACTION_BANS.UUID)
                            .values(factionName, player.getUniqueId().toString())
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Void> unbanMembers(List<OfflinePlayer> players, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (OfflinePlayer player : players) {
                    trx.dsl().deleteFrom(FACTION_BANS)
                            .where(FACTION_BANS.UUID.eq(player.getUniqueId().toString()), FACTION_BANS.FACTIONNAME.eq(factionName))
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<List<OfflinePlayer>> getBannedPlayers(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            Result<FactionBansRecord> results = ctx.selectFrom(FACTION_BANS)
                    .where(FACTION_BANS.FACTIONNAME.eq(factionName))
                    .fetch();

            List<OfflinePlayer> players = new ArrayList<>();
            for (FactionBansRecord bannedPlayer : results) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(bannedPlayer.getUuid()));
                players.add(player);
            }

            return players;
        });
    }

    public CompletableFuture<Boolean> isPlayerBanned(OfflinePlayer player, String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(FACTION_BANS, FACTION_BANS.FACTIONNAME.eq(factionName), FACTION_BANS.UUID.eq(player.getUniqueId().toString())));
    }

}
