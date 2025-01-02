package net.skullian.skyfactions.common.database.impl.faction;

import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.AbstractTableManager;
import net.skullian.skyfactions.common.database.tables.records.FactionIslandsRecord;
import net.skullian.skyfactions.common.island.IslandModificationAction;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.skullian.skyfactions.common.database.tables.FactionIslands.FACTION_ISLANDS;

public class FactionIslandDatabaseManager extends AbstractTableManager {

    public int cachedFactionIslandID;

    public FactionIslandDatabaseManager(DSLContext ctx, Executor executor) {
        super(ctx, executor);
        setFactionIslandCachedNextID();
    }

    public void setFactionIslandCachedNextID() {
        CompletableFuture.runAsync(() -> {
            Integer result = ctx.select(DSL.max(FACTION_ISLANDS.ID))
                    .from(FACTION_ISLANDS)
                    .fetchOneInto(Integer.class);

            if (result == null) this.cachedFactionIslandID = 1;
            else this.cachedFactionIslandID = (result + 1);
        }, executor);
    }

    public CompletableFuture<Void> createFactionIsland(String factionName, IslandModificationAction action) {
        return CompletableFuture.runAsync(() -> {
            if (action == null) return;
            ctx.insertInto(FACTION_ISLANDS)
                    .columns(FACTION_ISLANDS.ID, FACTION_ISLANDS.FACTIONNAME, FACTION_ISLANDS.RUNES, FACTION_ISLANDS.DEFENCECOUNT, FACTION_ISLANDS.GEMS, FACTION_ISLANDS.LAST_RAIDED, FACTION_ISLANDS.LAST_RAIDER)
                    .values(action.getId(), factionName, 0, 0, 0, System.currentTimeMillis() + Settings.RAIDING_FACTION_IMMUNITY.getLong(), "N/A".getBytes())
                    .execute();
        }, executor);
    }

    public CompletableFuture<FactionIsland> getFactionIsland(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            FactionIslandsRecord result = ctx.selectFrom(FACTION_ISLANDS)
                    .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                    .fetchOne();

            return result != null ? new FactionIsland(result.getId()) : null;
        }, executor);
    }

    public CompletableFuture<Void> updateFactionLastRaid(String factionName, long newTime) {
        return CompletableFuture.runAsync(() -> ctx.update(FACTION_ISLANDS)
                .set(FACTION_ISLANDS.LAST_RAIDED, newTime)
                .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                .execute(), executor);
    }


}
