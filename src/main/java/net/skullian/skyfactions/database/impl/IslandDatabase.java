package net.skullian.skyfactions.database.impl;

import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.island.PlayerIsland;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.Tables.ISLANDS;

public class IslandDatabase {

    private final DSLContext ctx;

    public int cachedPlayerIslandID;

    public IslandDatabase(DSLContext ctx) {
        this.ctx = ctx;
        setIslandCachedNextID();
    }

    public CompletableFuture<Boolean> hasIsland(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            Result<Record> result = ctx.selectFrom(ISLANDS)
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
           Result<Record> result = ctx.selectFrom(ISLANDS)
                   .where(ISLANDS.UUID.eq(playerUUID.toString()))
                   .fetch();

           if (result.isEmpty()) return null;

           return new PlayerIsland(result.getFirst().getValue(ISLANDS.ID));
        });
    }

    public CompletableFuture<Void> setIslandCachedNextID() {
        return CompletableFuture.runAsync(() -> {
            Integer result = ctx.select(DSL.max(ISLANDS.ID))
                    .from(ISLANDS)
                    .fetchOneInto(Integer.class);

            if (result == null) this.cachedPlayerIslandID = 0;
                else this.cachedPlayerIslandID = result;
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
}
