package net.skullian.skyfactions.database.impl.faction;

import net.skullian.skyfactions.database.struct.AuditLogData;
import net.skullian.skyfactions.database.tables.records.AuditlogsRecord;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Auditlogs.AUDITLOGS;

public class FactionAuditLogDatabaseManager {

    private final DSLContext ctx;

    public FactionAuditLogDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<Void> createAuditLog(UUID playerUUID, String type, String replacements, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(AUDITLOGS)
                    .columns(AUDITLOGS.FACTIONNAME, AUDITLOGS.TYPE, AUDITLOGS.UUID, AUDITLOGS.REPLACEMENTS, AUDITLOGS.TIMESTAMP)
                    .values(factionName, type, playerUUID.toString(), replacements, System.currentTimeMillis())
                    .execute();
        });
    }

    public CompletableFuture<List<AuditLogData>> getAuditLogs(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            Result<AuditlogsRecord> results = ctx.selectFrom(AUDITLOGS)
                    .where(AUDITLOGS.FACTIONNAME.eq(factionName))
                    .orderBy(AUDITLOGS.TIMESTAMP.desc())
                    .fetch();

            List<AuditLogData> data = new ArrayList<>();
            for (AuditlogsRecord log : results) {
                data.add(new AuditLogData(
                        Bukkit.getOfflinePlayer(UUID.fromString(log.getUuid())),
                        log.getFactionname(),
                        log.getType(),
                        TextUtility.convertFromString(log.getReplacements()),
                        log.getTimestamp()
                ));
            }

            return data;
        });
    }
}