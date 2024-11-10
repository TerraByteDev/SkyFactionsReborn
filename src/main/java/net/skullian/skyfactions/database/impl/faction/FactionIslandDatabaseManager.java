package net.skullian.skyfactions.database.impl.faction;

import net.skullian.skyfactions.database.tables.records.FactionislandsRecord;
import net.skullian.skyfactions.island.FactionIsland;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Factionislands.FACTIONISLANDS;

public class FactionIslandDatabaseManager {

    private final DSLContext ctx;

    public int cachedFactionIslandID;

    public FactionIslandDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
        setFactionIslandCachedNextID();
    }

    public CompletableFuture<Void> setFactionIslandCachedNextID() {
        return CompletableFuture.runAsync(() -> {
            Integer result = ctx.select(DSL.max(FACTIONISLANDS.ID))
                    .from(FACTIONISLANDS)
                    .fetchOneInto(Integer.class);

            if (result == null) this.cachedFactionIslandID = 1;
                else this.cachedFactionIslandID = (result + 1);
        });
    }

    public CompletableFuture<Void> createFactionIsland(String factionName, FactionIsland island) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(FACTIONISLANDS)
                    .columns(FACTIONISLANDS.ID, FACTIONISLANDS.FACTIONNAME, FACTIONISLANDS.RUNES, FACTIONISLANDS.DEFENCECOUNT, FACTIONISLANDS.GEMS, FACTIONISLANDS.LAST_RAIDED, FACTIONISLANDS.LAST_RAIDER)
                    .values(island.getId(), factionName, 0, 0, 0, island.getLast_raided(), "N/A")
                    .execute();
        });
    }

    public CompletableFuture<FactionIsland> getFactionIsland(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            FactionislandsRecord result = ctx.selectFrom(FACTIONISLANDS)
                    .where(FACTIONISLANDS.FACTIONNAME.eq(factionName))
                    .fetchOne();

            return result != null ? new FactionIsland(result.getId(), result.getLastRaided()) : null;
        });
    }


}
