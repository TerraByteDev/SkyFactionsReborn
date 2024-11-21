package net.skullian.skyfactions.database.impl;

import org.jooq.DSLContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Islands.ISLANDS;
import static net.skullian.skyfactions.database.tables.FactionIslands.FACTION_ISLANDS;

public class CurrencyDatabaseManager {

    private final DSLContext ctx;

    public CurrencyDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    // ------------------ GEMS  ------------------ //

    public CompletableFuture<Integer> getGems(UUID playerUUID) {


        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.GEMS)
                .from(ISLANDS)
                .where(ISLANDS.UUID.eq(playerUUID.toString()))
                .fetchOneInto(Integer.class));
    }

    public CompletableFuture<Integer> getGems(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.select(FACTION_ISLANDS.GEMS)
                .from(FACTION_ISLANDS)
                .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                .fetchOneInto(Integer.class));
    }

    public CompletableFuture<Void> modifyGems(UUID playerUUID, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            System.out.println("UUID: " + playerUUID);
            int current = getGems(playerUUID).join();

            ctx.update(ISLANDS)
                    .set(ISLANDS.GEMS, (subtract ? current - amount : current + amount))
                    .where(ISLANDS.UUID.eq(playerUUID.toString()))
                    .execute();
        });
    }

    public CompletableFuture<Void> modifyGems(String factionName, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            int current = getGems(factionName).join();

            ctx.update(FACTION_ISLANDS)
                    .set(FACTION_ISLANDS.GEMS, (subtract ? current - amount : current + amount))
                    .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                    .execute();
        });
    }

    // ------------------ RUNES  ------------------ //


    public CompletableFuture<Integer> getRunes(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> ctx.select(ISLANDS.RUNES)
                .from(ISLANDS)
                .where(ISLANDS.UUID.eq(playerUUID.toString()))
                .fetchOneInto(Integer.class));
    }

    public CompletableFuture<Integer> getRunes(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.select(FACTION_ISLANDS.RUNES)
                .from(FACTION_ISLANDS)
                .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                .fetchOneInto(Integer.class));
    }

    public CompletableFuture<Void> modifyRunes(UUID playerUUID, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            int current = getRunes(playerUUID).join();

            ctx.update(ISLANDS)
                    .set(ISLANDS.RUNES, (subtract ? current - amount : current + amount))
                    .where(ISLANDS.UUID.eq(playerUUID.toString()))
                    .execute();
        });
    }

    public CompletableFuture<Void> modifyRunes(String factionName, int amount, boolean subtract) {
        return CompletableFuture.runAsync(() -> {
            int current = getRunes(factionName).join();

            ctx.update(FACTION_ISLANDS)
                    .set(FACTION_ISLANDS.RUNES, (subtract ? current - amount : current + amount))
                    .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                    .execute();
        });
    }


}
