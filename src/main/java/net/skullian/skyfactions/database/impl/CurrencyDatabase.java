package net.skullian.skyfactions.database.impl;

import org.jooq.DSLContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Islands.ISLANDS;

public class CurrencyDatabase {

    private final DSLContext ctx;

    public int cachedPlayerIslandID;

    public CurrencyDatabase(DSLContext ctx) {
        this.ctx = ctx;
    }

    // ------------------ GEMS  ------------------ //

    public CompletableFuture<Integer> getGems(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.GEMS)
                .where(ISLANDS.UUID.eq(playerUUID.toString()))
                .fetchOneInto(Integer.class));
    }

    public CompletableFuture<Void> modifyGems(UUID playerUUID, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            int current = getGems(playerUUID).join();

            ctx.update(ISLANDS)
                    .set(ISLANDS.GEMS, (subtract ? current - amount : current + amount))
                    .where(ISLANDS.UUID.eq(playerUUID.toString()))
                    .execute();
        });
    }
    // ------------------ RUNES  ------------------ //


    public CompletableFuture<Integer> getRunes(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.RUNES)
                .where(ISLANDS.UUID.eq(playerUUID.toString()))
                .fetchOneInto(Integer.class));
    }


}
