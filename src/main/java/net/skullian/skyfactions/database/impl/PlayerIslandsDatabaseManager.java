package net.skullian.skyfactions.database.impl;

import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.database.struct.IslandRaidData;
import net.skullian.skyfactions.database.tables.records.IslandsRecord;
import net.skullian.skyfactions.database.tables.records.TrustedplayersRecord;
import net.skullian.skyfactions.island.PlayerIsland;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Islands.ISLANDS;
import static net.skullian.skyfactions.database.tables.Trustedplayers.TRUSTEDPLAYERS;

public class PlayerIslandsDatabaseManager {

    private final DSLContext ctx;

    public int cachedPlayerIslandID;

    public PlayerIslandsDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
        setIslandCachedNextID();
    }

    public CompletableFuture<Boolean> hasIsland(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            Result<IslandsRecord> result = ctx.selectFrom(ISLANDS)
                    .where(ISLANDS.UUID.eq(playerUUID.toString()))
                    .fetch();

            return !result.isEmpty();
        });
    }

    public CompletableFuture<Void> createIsland(Player player, PlayerIsland island) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(ISLANDS)
                    .columns(ISLANDS.ID, ISLANDS.UUID, ISLANDS.LEVEL, ISLANDS.GEMS, ISLANDS.RUNES, ISLANDS.DEFENCECOUNT, ISLANDS.LAST_RAIDED, ISLANDS.LAST_RAIDER)
                    .values(island.getId(), player.getUniqueId().toString(), 0, 0, 0, 0, System.currentTimeMillis() + Settings.RAIDING_PLAYER_IMMUNITY.getInt(), "N/A")
                    .execute();
        });
    }

    public CompletableFuture<UUID> getOwnerOfIsland(PlayerIsland island) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.UUID)
                .from(ISLANDS)
                .where(ISLANDS.ID.eq(island.getId()))
                .fetchOneInto(UUID.class));
    }

    public CompletableFuture<PlayerIsland> getPlayerIsland(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
           IslandsRecord result = ctx.selectFrom(ISLANDS)
                   .where(ISLANDS.UUID.eq(playerUUID.toString()))
                   .fetchOne();

           return result != null ? new PlayerIsland(result.getId()) : null;
        });
    }

    public CompletableFuture<Void> setIslandCachedNextID() {
        return CompletableFuture.runAsync(() -> {
            Integer result = ctx.select(DSL.max(ISLANDS.ID))
                    .from(ISLANDS)
                    .fetchOneInto(Integer.class);

            if (result == null) this.cachedPlayerIslandID = 1;
                else this.cachedPlayerIslandID = (result + 1);
        });
    }

    public CompletableFuture<Integer> getIslandLevel(PlayerIsland island) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.LEVEL)
                .from(ISLANDS)
                .where(ISLANDS.ID.eq(island.getId()))
                .fetchOneInto(Integer.class));
    }

    public CompletableFuture<Void> setIslandCooldown(PlayerIsland island, long time) {
        return CompletableFuture.runAsync(() -> {
            ctx.update(ISLANDS)
                    .set(ISLANDS.LAST_RAIDED, time)
                    .where(ISLANDS.ID.eq(island.getId()))
                    .execute();
        });
    }

    public CompletableFuture<Void> removeIsland(Player player) {
        SLogger.info("Removing island [{}] from Database.", player.getUniqueId().toString());
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(ISLANDS)
                    .where(ISLANDS.UUID.eq(player.getUniqueId().toString()))
                    .execute();
        });
    }

    public CompletableFuture<List<IslandRaidData>> getRaidableIslands(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            Result<IslandsRecord> results = ctx.selectFrom(ISLANDS)
                    .where(ISLANDS.LAST_RAIDED.lessOrEqual((System.currentTimeMillis() - Settings.RAIDED_COOLDOWN.getInt())))
                    .fetch();

            List<IslandRaidData> islands = new ArrayList<>();
            for (IslandsRecord data : results) {
                islands.add(new IslandRaidData(
                        data.get(ISLANDS.ID),
                        data.get(ISLANDS.UUID),
                        data.get(ISLANDS.LAST_RAIDED)
                ));
            }

            return islands;
        });
    }

    // ------------------ TRUSTING ------------------ //

    public CompletableFuture<Boolean> isPlayerTrusted(UUID playerUUID, int id) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(TRUSTEDPLAYERS, TRUSTEDPLAYERS.UUID.eq(playerUUID.toString()), TRUSTEDPLAYERS.ISLAND_ID.eq(id)));
    }

    public CompletableFuture<Void> trustPlayer(UUID playerUUID, int islandID) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(TRUSTEDPLAYERS)
                    .columns(TRUSTEDPLAYERS.ISLAND_ID, TRUSTEDPLAYERS.UUID)
                    .values(islandID, playerUUID.toString())
                    .execute();
        });
    }

    public CompletableFuture<Void> removePlayerTrust(UUID playerUUID, int islandID) {
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(TRUSTEDPLAYERS)
                    .where(TRUSTEDPLAYERS.ISLAND_ID.eq(islandID), TRUSTEDPLAYERS.UUID.eq(playerUUID.toString()))
                    .execute();
        });
    }

    public CompletableFuture<Void> removeAllTrustedPlayers(int islandID) {
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(TRUSTEDPLAYERS)
                    .where(TRUSTEDPLAYERS.ISLAND_ID.eq(islandID))
                    .execute();
        });
    }

    public CompletableFuture<List<OfflinePlayer>> getTrustedPlayers(int islandID) {
        return CompletableFuture.supplyAsync(() -> {
            Result<TrustedplayersRecord> results = ctx.selectFrom(TRUSTEDPLAYERS)
                    .where(TRUSTEDPLAYERS.ISLAND_ID.eq(islandID))
                    .fetch();

            List<OfflinePlayer> players = new ArrayList<>();
            for (TrustedplayersRecord data : results) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(data.get(TRUSTEDPLAYERS.UUID)));

                if (player.hasPlayedBefore()) {
                    players.add(player);
                }
            }

            return players;
        });
    }
}
