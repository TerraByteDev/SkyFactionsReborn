package net.skullian.skyfactions.database.impl.faction;

import net.skullian.skyfactions.database.tables.records.FactionElectionsRecord;
import org.jooq.DSLContext;

import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.FactionElections.FACTION_ELECTIONS;

public class FactionElectionManager {
    private final DSLContext ctx;

    public FactionElectionManager(DSLContext ctx) {
        this.ctx = ctx;
    }


    public CompletableFuture<Boolean> isElectionRunning(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(FACTION_ELECTIONS, FACTION_ELECTIONS.FACTIONNAME.eq(factionName)));
    }

    public CompletableFuture<Integer> getElectionID(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            return ctx.select(FACTION_ELECTIONS.ID)
                    .from(FACTION_ELECTIONS)
                    .where(FACTION_ELECTIONS.FACTIONNAME.eq(factionName))
                    .fetchOneInto(Integer.class);
        });
    }
}
