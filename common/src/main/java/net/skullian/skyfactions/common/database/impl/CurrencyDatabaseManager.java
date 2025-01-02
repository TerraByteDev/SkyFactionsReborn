package net.skullian.skyfactions.common.database.impl;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.AbstractTableManager;
import org.jooq.DSLContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.skullian.skyfactions.common.database.tables.Islands.ISLANDS;
import static net.skullian.skyfactions.common.database.tables.FactionIslands.FACTION_ISLANDS;

public class CurrencyDatabaseManager extends AbstractTableManager {

    public CurrencyDatabaseManager(DSLContext ctx, Executor executor) {
        super(ctx, executor);
    }

    // ------------------ GEMS  ------------------ //

    public CompletableFuture<Integer> getGems(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.GEMS)
                .from(ISLANDS)
                .where(ISLANDS.UUID.eq(fromUUID(playerUUID)))
                .fetchOneInto(Integer.class), executor);
    }

    public CompletableFuture<Integer> getGems(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.select(FACTION_ISLANDS.GEMS)
                .from(FACTION_ISLANDS)
                .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                .fetchOneInto(Integer.class), executor);
    }

    public CompletableFuture<Void> modifyGems(UUID playerUUID, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            if (amount == 0 || !SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().hasIsland(playerUUID).join()) return;
            int current = getGems(playerUUID).join();

            ctx.update(ISLANDS)
                    .set(ISLANDS.GEMS, (subtract ? current - amount : current + amount))
                    .where(ISLANDS.UUID.eq(fromUUID(playerUUID)))
                    .execute();
        }, executor);
    }

    public CompletableFuture<Void> modifyGems(String factionName, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            if (amount == 0) return;
            int current = getGems(factionName).join();

            ctx.update(FACTION_ISLANDS)
                    .set(FACTION_ISLANDS.GEMS, (subtract ? current - amount : current + amount))
                    .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                    .execute();
        }, executor);
    }

    // ------------------ RUNES  ------------------ //


    public CompletableFuture<Integer> getRunes(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.RUNES)
                .from(ISLANDS)
                .where(ISLANDS.UUID.eq(fromUUID(playerUUID)))
                .fetchOneInto(Integer.class), executor);
    }

    public CompletableFuture<Integer> getRunes(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.select(FACTION_ISLANDS.RUNES)
                .from(FACTION_ISLANDS)
                .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                .fetchOneInto(Integer.class), executor);
    }

    public CompletableFuture<Void> modifyRunes(UUID playerUUID, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            if (amount == 0 || !SkyApi.getInstance().getDatabaseManager().getPlayerIslandManager().hasIsland(playerUUID).join()) return;
            int current = getRunes(playerUUID).join();

            ctx.update(ISLANDS)
                    .set(ISLANDS.RUNES, (subtract ? current - amount : current + amount))
                    .where(ISLANDS.UUID.eq(fromUUID(playerUUID)))
                    .execute();
        }, executor);
    }

    public CompletableFuture<Void> modifyRunes(String factionName, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            if (amount == 0) return;
            int current = getRunes(factionName).join();

            ctx.update(FACTION_ISLANDS)
                    .set(FACTION_ISLANDS.RUNES, (subtract ? current - amount : current + amount))
                    .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                    .execute();
        }, executor);
    }


}
