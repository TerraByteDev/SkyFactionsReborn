package net.skullian.skyfactions.common.database.impl;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.AbstractTableManager;
import net.skullian.skyfactions.common.database.struct.IslandRaidData;
import net.skullian.skyfactions.common.database.tables.records.IslandsRecord;
import net.skullian.skyfactions.common.database.tables.records.TrustedPlayersRecord;
import net.skullian.skyfactions.common.island.IslandModificationAction;
import net.skullian.skyfactions.common.island.SkyIsland;
import net.skullian.skyfactions.common.island.impl.PlayerIsland;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.skullian.skyfactions.common.database.tables.Islands.ISLANDS;
import static net.skullian.skyfactions.common.database.tables.TrustedPlayers.TRUSTED_PLAYERS;

public class PlayerIslandsDatabaseManager extends AbstractTableManager {

    public int cachedPlayerIslandID;

    public PlayerIslandsDatabaseManager(DSLContext ctx, Executor executor) {
        super(ctx, executor);
        setIslandCachedNextID();
    }

    public CompletableFuture<Boolean> hasIsland(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            Result<IslandsRecord> result = ctx.selectFrom(ISLANDS)
                    .where(ISLANDS.UUID.eq(fromUUID(playerUUID)))
                    .fetch();

            return !result.isEmpty();
        }, executor);
    }

    public CompletableFuture<Void> createIsland(UUID playerUUID, IslandModificationAction action) {
        return CompletableFuture.runAsync(() -> {
            if (action == null) return;
            ctx.insertInto(ISLANDS)
                    .columns(ISLANDS.ID, ISLANDS.UUID, ISLANDS.LEVEL, ISLANDS.GEMS, ISLANDS.RUNES, ISLANDS.DEFENCECOUNT, ISLANDS.LAST_RAIDED, ISLANDS.LAST_RAIDER, ISLANDS.CREATED)
                    .values(action.getId(), fromUUID(playerUUID), 0, 0, 0, 0, System.currentTimeMillis() + Settings.RAIDING_PLAYER_IMMUNITY.getLong(), "N/A".getBytes(), System.currentTimeMillis())
                    .execute();
        }, executor);
    }

    public CompletableFuture<UUID> getOwnerOfIsland(SkyIsland island) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.UUID)
                .from(ISLANDS)
                .where(ISLANDS.ID.eq(island.getId()))
                .fetchOneInto(UUID.class), executor);
    }

    public CompletableFuture<PlayerIsland> getPlayerIsland(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
           IslandsRecord result = ctx.selectFrom(ISLANDS)
                   .where(ISLANDS.UUID.eq(fromUUID(playerUUID)))
                   .fetchOne();

           return result != null ? new PlayerIsland(result.getId()) : null;
        }, executor);
    }

    public void setIslandCachedNextID() {
        CompletableFuture.runAsync(() -> {
            Integer result = ctx.select(DSL.max(ISLANDS.ID))
                    .from(ISLANDS)
                    .fetchOneInto(Integer.class);

            if (result == null) this.cachedPlayerIslandID = 1;
            else this.cachedPlayerIslandID = (result + 1);
        }, executor);
    }

    public CompletableFuture<Integer> getIslandLevel(SkyIsland island) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.LEVEL)
                .from(ISLANDS)
                .where(ISLANDS.ID.eq(island.getId()))
                .fetchOneInto(Integer.class), executor);
    }

    public CompletableFuture<Void> setIslandCooldown(SkyIsland island, long time) {
        return CompletableFuture.runAsync(() -> {
            ctx.update(ISLANDS)
                    .set(ISLANDS.LAST_RAIDED, time)
                    .where(ISLANDS.ID.eq(island.getId()))
                    .execute();
        }, executor);
    }

    public CompletableFuture<Void> removeIsland(SkyUser player) {
        SLogger.info("Removing island [{}] from Database.", player.getUniqueId());
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(ISLANDS)
                    .where(ISLANDS.UUID.eq(fromUUID(player.getUniqueId())))
                    .execute();
        }, executor);
    }

    public CompletableFuture<List<IslandRaidData>> getRaidableIslands(SkyUser player) {
        return CompletableFuture.supplyAsync(() -> {
            Result<IslandsRecord> results = ctx.selectFrom(ISLANDS)
                    .where(ISLANDS.LAST_RAIDED.lessOrEqual((System.currentTimeMillis() - Settings.RAIDED_COOLDOWN.getLong())))
                    .fetch();

            List<IslandRaidData> islands = new ArrayList<>();
            for (IslandsRecord data : results) {
                islands.add(new IslandRaidData(
                        data.getId(),
                        fromBytes(data.getUuid()).toString(),
                        data.getLastRaided()
                ));
            }

            return islands;
        }, executor);
    }

    // ------------------ TRUSTING ------------------ //

    public CompletableFuture<Boolean> isPlayerTrusted(UUID playerUUID, int id) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(TRUSTED_PLAYERS, TRUSTED_PLAYERS.UUID.eq(fromUUID(playerUUID)), TRUSTED_PLAYERS.ISLAND_ID.eq(id)), executor);
    }

    public CompletableFuture<Void> trustPlayer(UUID playerUUID, int islandID) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(TRUSTED_PLAYERS)
                    .columns(TRUSTED_PLAYERS.ISLAND_ID, TRUSTED_PLAYERS.UUID)
                    .values(islandID, fromUUID(playerUUID))
                    .execute();
        }, executor);
    }

    public CompletableFuture<Void> removePlayerTrust(UUID playerUUID, int islandID) {
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(TRUSTED_PLAYERS)
                    .where(TRUSTED_PLAYERS.ISLAND_ID.eq(islandID), TRUSTED_PLAYERS.UUID.eq(fromUUID(playerUUID)))
                    .execute();
        }, executor);
    }

    public CompletableFuture<Void> removeAllTrustedPlayers(int islandID) {
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(TRUSTED_PLAYERS)
                    .where(TRUSTED_PLAYERS.ISLAND_ID.eq(islandID))
                    .execute();
        }, executor);
    }

    public CompletableFuture<List<SkyUser>> getTrustedPlayers(int islandID) {
        return CompletableFuture.supplyAsync(() -> {
            Result<TrustedPlayersRecord> results = ctx.selectFrom(TRUSTED_PLAYERS)
                    .where(TRUSTED_PLAYERS.ISLAND_ID.eq(islandID))
                    .fetch();

            List<SkyUser> players = new ArrayList<>();
            for (TrustedPlayersRecord data : results) {
                SkyUser player = SkyApi.getInstance().getUserManager().getUser(fromBytes(data.getUuid()));
                players.add(player);
            }

            return players;
        }, executor);
    }
}
