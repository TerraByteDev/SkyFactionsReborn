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
        return CompletableFuture.supplyAsync(() -> {
            FactionElectionsRecord election = ctx.selectFrom(FACTION_ELECTIONS)
                    .where(FACTION_ELECTIONS.FACTION_NAME.eq(factionName))
                    .fetchOne();
            return election != null;
        });
    }
}
