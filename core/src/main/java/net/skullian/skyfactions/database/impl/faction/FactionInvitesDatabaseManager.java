package net.skullian.skyfactions.database.impl.faction;

import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.database.tables.records.FactionInvitesRecord;
import net.skullian.skyfactions.faction.JoinRequestData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.FactionInvites.FACTION_INVITES;

public class FactionInvitesDatabaseManager {

    private final DSLContext ctx;

    public FactionInvitesDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    // ------------------ INVITES  ------------------ //

    public CompletableFuture<Void> createFactionInvites(List<InviteData> invites) {
        return CompletableFuture.runAsync(() -> {
            if (invites.isEmpty()) return;
            ctx.transaction((Configuration trx) -> {
                for (InviteData invite : invites) {
                    trx.dsl().insertInto(FACTION_INVITES)
                            .columns(FACTION_INVITES.FACTIONNAME, FACTION_INVITES.UUID, FACTION_INVITES.INVITER, FACTION_INVITES.TYPE, FACTION_INVITES.TIMESTAMP)
                            .values(invite.getFactionName(), invite.getPlayer().getUniqueId().toString(), (invite.getInviter() != null ? invite.getInviter().getUniqueId().toString() : ""), invite.getType(), invite.getTimestamp())
                            .execute();
                }
            });
        });
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
                        Bukkit.getOfflinePlayer(UUID.fromString(record.getUuid())),
                        Bukkit.getOfflinePlayer(UUID.fromString(record.getInviter())),
                        record.getFactionname(),
                        record.getType(),
                        record.getTimestamp()
                ));
            }

            return data;
        });
    }

    public CompletableFuture<List<InviteData>> getInvitesOfPlayer(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            Result<FactionInvitesRecord> results = ctx.selectFrom(FACTION_INVITES)
                    .where(FACTION_INVITES.UUID.eq(player.getUniqueId().toString()), FACTION_INVITES.TYPE.eq("outgoing"))
                    .orderBy(FACTION_INVITES.TIMESTAMP.desc())
                    .fetch();

            List<InviteData> data = new ArrayList<>();
            for (FactionInvitesRecord record : results) {
                data.add(new InviteData(
                        Bukkit.getOfflinePlayer(UUID.fromString(record.getUuid())),
                        Bukkit.getOfflinePlayer(UUID.fromString(record.getInviter())),
                        record.getFactionname(),
                        record.getType(),
                        record.getTimestamp()
                ));
            }

            return data;
        });
    }

    public CompletableFuture<JoinRequestData> getPlayerJoinRequest(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            FactionInvitesRecord result = ctx.selectFrom(FACTION_INVITES)
                    .where(FACTION_INVITES.UUID.eq(player.getUniqueId().toString()), FACTION_INVITES.TYPE.eq("incoming"))
                    .fetchOne();

            return result != null ? new JoinRequestData(
                    result.getFactionname(),
                    false,
                    result.getTimestamp()
            ) : null;
        });
    }

    public CompletableFuture<Void> removeInvites(List<InviteData> invites) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (InviteData invite : invites) {
                    trx.dsl().deleteFrom(FACTION_INVITES)
                            .where(FACTION_INVITES.FACTIONNAME.eq(invite.getFactionName()), FACTION_INVITES.UUID.eq(invite.getPlayer().getUniqueId().toString()), FACTION_INVITES.TYPE.eq(invite.getType()))
                            .execute();
                }
            });
        });
    }
}