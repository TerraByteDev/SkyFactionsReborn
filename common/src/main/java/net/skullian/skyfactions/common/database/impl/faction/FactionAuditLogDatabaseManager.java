package net.skullian.skyfactions.common.database.impl.faction;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.AbstractTableManager;
import net.skullian.skyfactions.common.database.struct.AuditLogData;
import net.skullian.skyfactions.common.database.tables.records.AuditLogsRecord;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.skullian.skyfactions.common.database.tables.AuditLogs.AUDIT_LOGS;

public class FactionAuditLogDatabaseManager extends AbstractTableManager {

    public FactionAuditLogDatabaseManager(DSLContext ctx, Executor executor) {
        super(ctx, executor);
    }

    public CompletableFuture<Void> createAuditLogs(List<AuditLogData> auditLogs) {
        return CompletableFuture.runAsync(() -> {
            if (auditLogs.isEmpty()) return;
            ctx.transaction((Configuration trx) -> {
                for (AuditLogData auditLog : auditLogs) {
                    trx.dsl().insertInto(AUDIT_LOGS)
                            .columns(AUDIT_LOGS.FACTIONNAME, AUDIT_LOGS.TYPE, AUDIT_LOGS.UUID, AUDIT_LOGS.REPLACEMENTS, AUDIT_LOGS.TIMESTAMP)
                            .values(auditLog.getFactionName(), auditLog.getType(), fromUUID(auditLog.getPlayer().getUniqueId()), Arrays.toString(auditLog.getReplacements()), System.currentTimeMillis())
                            .execute();
                }
            });
        }, executor);
    }

    public CompletableFuture<List<AuditLogData>> getAuditLogs(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            Result<AuditLogsRecord> results = ctx.selectFrom(AUDIT_LOGS)
                    .where(AUDIT_LOGS.FACTIONNAME.eq(factionName))
                    .orderBy(AUDIT_LOGS.TIMESTAMP.desc())
                    .fetch();

            List<AuditLogData> data = new ArrayList<>();
            for (AuditLogsRecord log : results) {
                data.add(new AuditLogData(
                        SkyApi.getInstance().getUserManager().getUser(fromBytes(log.getUuid())),
                        log.getFactionname(),
                        log.getType(),
                        TextUtility.convertFromString(log.getReplacements()),
                        log.getTimestamp()
                ));
            }

            return data;
        }, executor);
    }
}
