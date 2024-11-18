package net.skullian.skyfactions.database.impl.faction;

import net.skullian.skyfactions.database.tables.records.FactionelectionsRecord;
import org.jooq.DSLContext;

import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Factionelections.FACTIONELECTIONS;

public class FactionElectionManager {
    private final DSLContext ctx;

    public FactionElectionManager(DSLContext ctx) {
        this.ctx = ctx;
    }


    public CompletableFuture<Boolean> isElectionRunning(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            FactionelectionsRecord election = ctx.selectFrom(FACTIONELECTIONS)
                    .where(FACTIONELECTIONS.FACTIONNAME.eq(factionName))
                    .fetchOne();
            return election != null;
        });
    }
}
