package net.skullian.skyfactions.database.impl.faction;

import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.database.tables.records.FactionIslandsRecord;
import net.skullian.skyfactions.island.IslandModificationAction;
import net.skullian.skyfactions.island.SkyIsland;
import net.skullian.skyfactions.island.impl.FactionIsland;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.FactionIslands.FACTION_ISLANDS;

public class FactionIslandDatabaseManager {

    private final DSLContext ctx;

    public int cachedFactionIslandID;

    public FactionIslandDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
        setFactionIslandCachedNextID();
    }

    public CompletableFuture<Void> setFactionIslandCachedNextID() {
        return CompletableFuture.runAsync(() -> {
            Integer result = ctx.select(DSL.max(FACTION_ISLANDS.ID))
                    .from(FACTION_ISLANDS)
                    .fetchOneInto(Integer.class);

            if (result == null) this.cachedFactionIslandID = 1;
                else this.cachedFactionIslandID = (result + 1);
        });
    }

    public CompletableFuture<Void> createFactionIsland(String factionName, IslandModificationAction action) {
        return CompletableFuture.runAsync(() -> {
            if (action == null) return;
            ctx.insertInto(FACTION_ISLANDS)
                    .columns(FACTION_ISLANDS.ID, FACTION_ISLANDS.FACTIONNAME, FACTION_ISLANDS.RUNES, FACTION_ISLANDS.DEFENCECOUNT, FACTION_ISLANDS.GEMS, FACTION_ISLANDS.LAST_RAIDED, FACTION_ISLANDS.LAST_RAIDER)
                    .values(action.getId(), factionName, 0, 0, 0, System.currentTimeMillis() + Settings.RAIDING_FACTION_IMMUNITY.getLong(), "N/A")
                    .execute();
        });
    }

    public CompletableFuture<FactionIsland> getFactionIsland(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            FactionIslandsRecord result = ctx.selectFrom(FACTION_ISLANDS)
                    .where(FACTION_ISLANDS.FACTIONNAME.eq(factionName))
                    .fetchOne();

            return result != null ? new FactionIsland(result.getId()) : null;
        });
    }


}
