package net.skullian.skyfactions.common.database.impl.faction;

import net.skullian.skyfactions.common.database.AbstractTableManager;
import org.jooq.DSLContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.skullian.skyfactions.common.database.tables.FactionElections.FACTION_ELECTIONS;

public class FactionElectionManager extends AbstractTableManager {

    public FactionElectionManager(DSLContext ctx, Executor executor) {
        super(ctx, executor);
    }

    public CompletableFuture<Boolean> isElectionRunning(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(FACTION_ELECTIONS, FACTION_ELECTIONS.FACTIONNAME.eq(factionName)), executor);
    }

    public CompletableFuture<Integer> getElectionID(String factionName) {
        return CompletableFuture.supplyAsync(() -> ctx.select(FACTION_ELECTIONS.ID)
                .from(FACTION_ELECTIONS)
                .where(FACTION_ELECTIONS.FACTIONNAME.eq(factionName))
                .fetchOneInto(Integer.class),

                executor);
    }
}
