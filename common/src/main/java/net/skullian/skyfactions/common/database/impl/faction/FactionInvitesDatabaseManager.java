package net.skullian.skyfactions.common.database.impl.faction;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.AbstractTableManager;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.database.tables.records.FactionInvitesRecord;
import net.skullian.skyfactions.common.faction.JoinRequestData;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.skullian.skyfactions.common.database.tables.FactionInvites.FACTION_INVITES;

public class FactionInvitesDatabaseManager extends AbstractTableManager {

    public FactionInvitesDatabaseManager(DSLContext ctx, Executor executor) {
        super(ctx, executor);
    }

    // ------------------ INVITES  ------------------ //

    public CompletableFuture<Void> createFactionInvites(List<InviteData> invites) {
        return CompletableFuture.runAsync(() -> {
            if (invites.isEmpty()) return;
            ctx.transaction((Configuration trx) -> {
                for (InviteData invite : invites) {
                    trx.dsl().insertInto(FACTION_INVITES)
                            .columns(FACTION_INVITES.FACTIONNAME, FACTION_INVITES.UUID, FACTION_INVITES.INVITER, FACTION_INVITES.TYPE, FACTION_INVITES.TIMESTAMP)
                            .values(invite.getFactionName(), fromUUID(invite.getPlayer().getUniqueId()), (invite.getInviter() != null ? fromUUID(invite.getInviter().getUniqueId()) : "".getBytes()), invite.getType(), invite.getTimestamp())
                            .execute();
                }
            });
        }, executor);
    }

    public CompletableFuture<List<InviteData>> getAllInvites(String factionName) {
        return CompletableFuture.supplyAsync(() -> {
            Result<FactionInvitesRecord> results = ctx.selectFrom(FACTION_INVITES)
                    .where(FACTION_INVITES.FACTIONNAME.eq(factionName))
                    .orderBy(FACTION_INVITES.TIMESTAMP.desc())
                    .fetch();

            List<InviteData> data = new ArrayList<>();
            for (FactionInvitesRecord record : results) {
                data.add(new InviteData(
                        SkyApi.getInstance().getUserManager().getUser(fromBytes(record.getUuid())),
                        SkyApi.getInstance().getUserManager().getUser(fromBytes(record.getInviter())),
                        record.getFactionname(),
                        record.getType(),
                        record.getTimestamp()
                ));
            }

            return data;
        }, executor);
    }

    public CompletableFuture<List<InviteData>> getInvitesOfPlayer(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            Result<FactionInvitesRecord> results = ctx.selectFrom(FACTION_INVITES)
                    .where(FACTION_INVITES.UUID.eq(fromUUID(playerUUID)), FACTION_INVITES.TYPE.eq("outgoing"))
                    .orderBy(FACTION_INVITES.TIMESTAMP.desc())
                    .fetch();

            List<InviteData> data = new ArrayList<>();
            for (FactionInvitesRecord record : results) {
                data.add(new InviteData(
                        SkyApi.getInstance().getUserManager().getUser(fromBytes(record.getUuid())),
                        SkyApi.getInstance().getUserManager().getUser(fromBytes(record.getInviter())),
                        record.getFactionname(),
                        record.getType(),
                        record.getTimestamp()
                ));
            }

            return data;
        }, executor);
    }

    public CompletableFuture<JoinRequestData> getPlayerJoinRequest(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> {
            FactionInvitesRecord result = ctx.selectFrom(FACTION_INVITES)
                    .where(FACTION_INVITES.UUID.eq(fromUUID(playerUUID)), FACTION_INVITES.TYPE.eq("incoming"))
                    .fetchOne();

            return result != null ? new JoinRequestData(
                    result.getFactionname(),
                    false,
                    result.getTimestamp()
            ) : null;
        }, executor);
    }

    public CompletableFuture<Void> removeInvites(List<InviteData> invites) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (InviteData invite : invites) {
                    trx.dsl().deleteFrom(FACTION_INVITES)
                            .where(FACTION_INVITES.FACTIONNAME.eq(invite.getFactionName()), FACTION_INVITES.UUID.eq(fromUUID(invite.getPlayer().getUniqueId())), FACTION_INVITES.TYPE.eq(invite.getType()))
                            .execute();
                }
            });
        }, executor);
    }
}
