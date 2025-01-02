package net.skullian.skyfactions.common.database.impl.faction;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.AbstractTableManager;
import net.skullian.skyfactions.common.database.tables.records.FactionBansRecord;
import net.skullian.skyfactions.common.database.tables.records.FactionIslandsRecord;
import net.skullian.skyfactions.common.database.tables.records.FactionMembersRecord;
import net.skullian.skyfactions.common.database.tables.records.FactionsRecord;
import net.skullian.skyfactions.common.faction.Faction;
import net.skullian.skyfactions.common.faction.RankType;
import net.skullian.skyfactions.common.user.SkyUser;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.skullian.skyfactions.common.database.tables.DefenceLocations.DEFENCE_LOCATIONS;
import static net.skullian.skyfactions.common.database.tables.FactionBans.FACTION_BANS;
import static net.skullian.skyfactions.common.database.tables.FactionInvites.FACTION_INVITES;
import static net.skullian.skyfactions.common.database.tables.FactionIslands.FACTION_ISLANDS;
import static net.skullian.skyfactions.common.database.tables.FactionMembers.FACTION_MEMBERS;
import static net.skullian.skyfactions.common.database.tables.Factions.FACTIONS;
import static net.skullian.skyfactions.common.database.tables.AuditLogs.AUDIT_LOGS;
import static net.skullian.skyfactions.common.database.tables.FactionElections.FACTION_ELECTIONS;
import static net.skullian.skyfactions.common.database.tables.ElectionVotes.ELECTION_VOTES;

public class FactionsDatabaseManager extends AbstractTableManager {

    public FactionsDatabaseManager(DSLContext ctx, Executor executor) {
        super(ctx, executor);
    }

    public CompletableFuture<Void> registerFaction(SkyUser factionOwner, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(FACTIONS)
                    .columns(FACTIONS.NAME, FACTIONS.MOTD, FACTIONS.LEVEL, FACTIONS.LAST_RAID, FACTIONS.LOCALE, FACTIONS.LAST_RENAMED)
                    .values(factionName, "<red>None", 1, (long) 0, SkyApi.getInstance().getPlayerAPI().getLocale(factionOwner.getUniqueId()), System.currentTimeMillis())
                    .execute();

            ctx.insertInto(FACTION_MEMBERS)
                    .columns(FACTION_MEMBERS.FACTIONNAME, FACTION_MEMBERS.UUID, FACTION_MEMBERS.RANK)
                    .values(factionName, fromUUID(factionOwner.getUniqueId()), "owner")
                    .execute();
        }, executor);
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
        }, executor);
    }

    public CompletableFuture<Faction> getFaction(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            FactionMembersRecord result = ctx.selectFrom(FACTION_MEMBERS)
                    .where(FACTION_MEMBERS.UUID.eq(fromUUID(playerUUID)))
                    .fetchOne();

            return result != null ? getFaction(result.getFactionname()).join() : null;
        }, executor);
    }

    public CompletableFuture<Faction> getFactionByIslandID(int id) {
        return CompletableFuture.supplyAsync(() -> {
            FactionIslandsRecord result = ctx.selectFrom(FACTION_ISLANDS)
                    .where(FACTION_ISLANDS.ID.eq(id))
                    .fetchOne();

            return result != null ? getFaction(result.getFactionname()).join() : null;
        }, executor);
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
        }, executor);
    }

    public CompletableFuture<String> getFactionMOTD(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.select(FACTIONS.MOTD)
                .from(FACTIONS)
                .where(FACTIONS.NAME.eq(factionName))
                .fetchOneInto(String.class), executor);
    }

    public CompletableFuture<Void> updateFactionMOTD(String factionName, String newMOTD) {
        return CompletableFuture.runAsync(() -> {
            ctx.update(FACTIONS)
                    .set(FACTIONS.MOTD, newMOTD)
                    .where(FACTIONS.NAME.eq(factionName))
                    .execute();
        }, executor);
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
        }, executor);
    }


    // ------------------ MEMBERS  ------------------ //

    public CompletableFuture<Void> addFactionMembers(List<SkyUser> players, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (SkyUser player : players) {
                    trx.dsl().insertInto(FACTION_MEMBERS)
                            .columns(FACTION_MEMBERS.FACTIONNAME, FACTION_MEMBERS.UUID, FACTION_MEMBERS.RANK)
                            .values(factionName, fromUUID(player.getUniqueId()), "member")
                            .execute();
                }
            });
        }, executor);
    }

    public CompletableFuture<Void> updateFactionMemberRanks(String factionName, Map<UUID, RankType> ranks) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (Map.Entry<UUID, RankType> entry : ranks.entrySet()) {
                    trx.dsl().update(FACTION_MEMBERS)
                            .set(FACTION_MEMBERS.RANK, entry.getValue().getRankValue())
                            .where(FACTION_MEMBERS.UUID.eq(fromUUID(entry.getKey())), FACTION_MEMBERS.FACTIONNAME.eq(factionName))
                            .execute();
                }
            });
        }, executor);
    }

    public CompletableFuture<Boolean> isInFaction(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(FACTION_MEMBERS, FACTION_MEMBERS.UUID.eq(fromUUID(playerUUID))), executor);
    }

    public CompletableFuture<SkyUser> getFactionOwner(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            FactionMembersRecord result = ctx.selectFrom(FACTION_MEMBERS)
                    .where(FACTION_MEMBERS.FACTIONNAME.eq(factionName), FACTION_MEMBERS.RANK.eq("owner"))
                    .fetchOne();

            return result != null ? SkyApi.getInstance().getUserManager().getUser(fromBytes(result.getUuid())) : null;
        }, executor);
    }

    public CompletableFuture<List<SkyUser>> getFactionMembersByRank(String factionName, RankType rank) {
        return CompletableFuture.supplyAsync(() -> {
            Result<FactionMembersRecord> results = ctx.selectFrom(FACTION_MEMBERS)
                    .where(FACTION_MEMBERS.FACTIONNAME.eq(factionName), FACTION_MEMBERS.RANK.eq(rank.getRankValue()))
                    .fetch();

            List<SkyUser> players = new ArrayList<>();
            for (FactionMembersRecord member : results) {
                SkyUser player = SkyApi.getInstance().getUserManager().getUser(fromBytes(member.getUuid()));
                players.add(player);
            }

            return players;
        }, executor);
    }

    // ------------------ ADMINISTRATION  ------------------ //

    public CompletableFuture<Void> removeMembers(List<SkyUser> players, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (SkyUser player : players) {
                    trx.dsl().deleteFrom(FACTION_MEMBERS)
                            .where(FACTION_MEMBERS.UUID.eq(fromUUID(player.getUniqueId())), FACTION_MEMBERS.FACTIONNAME.eq(factionName))
                            .execute();
                }
            });
        }, executor);
    }

    public CompletableFuture<Void> banMembers(List<SkyUser> players, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                removeMembers(players, factionName).join();

                for (SkyUser player : players) {
                    trx.dsl().insertInto(FACTION_BANS)
                            .columns(FACTION_BANS.FACTIONNAME, FACTION_BANS.UUID)
                            .values(factionName, fromUUID(player.getUniqueId()))
                            .execute();
                }
            });
        }, executor);
    }

    public CompletableFuture<Void> unbanMembers(List<SkyUser> players, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (SkyUser player : players) {
                    trx.dsl().deleteFrom(FACTION_BANS)
                            .where(FACTION_BANS.UUID.eq(fromUUID(player.getUniqueId())), FACTION_BANS.FACTIONNAME.eq(factionName))
                            .execute();
                }
            });
        }, executor);
    }

    public CompletableFuture<List<SkyUser>> getBannedPlayers(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            Result<FactionBansRecord> results = ctx.selectFrom(FACTION_BANS)
                    .where(FACTION_BANS.FACTIONNAME.eq(factionName))
                    .fetch();

            List<SkyUser> players = new ArrayList<>();
            for (FactionBansRecord bannedPlayer : results) {
                SkyUser player = SkyApi.getInstance().getUserManager().getUser(fromBytes(bannedPlayer.getUuid()));
                players.add(player);
            }

            return players;
        }, executor);
    }

    public CompletableFuture<Boolean> isPlayerBanned(SkyUser player, String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(FACTION_BANS, FACTION_BANS.FACTIONNAME.eq(factionName), FACTION_BANS.UUID.eq(fromUUID(player.getUniqueId()))), executor);
    }

}
