package net.skullian.skyfactions.database.impl.faction;

import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.database.tables.records.FactioninvitesRecord;
import net.skullian.skyfactions.faction.JoinRequestData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Factioninvites.FACTIONINVITES;

public class FactionInvitesManager {

    private final DSLContext ctx;

    public FactionInvitesManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    // ------------------ INVITES  ------------------ //

    public CompletableFuture<Void> createFactionInvite(UUID invitedPlayerUUID, String factionName, String type, Player inviter) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(FACTIONINVITES)
                    .columns(FACTIONINVITES.FACTIONNAME, FACTIONINVITES.UUID, FACTIONINVITES.INVITER, FACTIONINVITES.TYPE, FACTIONINVITES.ACCEPTED, FACTIONINVITES.TIMESTAMP)
                    .values(factionName, invitedPlayerUUID.toString(), (inviter != null ? inviter.getUniqueId().toString() : ""), type, false, System.currentTimeMillis())
                    .execute();
        });
    }

    public CompletableFuture<JoinRequestData> getPlayerOutgoingJoinRequest(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            FactioninvitesRecord result = ctx.selectFrom(FACTIONINVITES)
                    .where(FACTIONINVITES.UUID.eq(player.getUniqueId().toString()), FACTIONINVITES.TYPE.eq("incoming"))
                    .fetchOne();

            return result != null ? new JoinRequestData(result.getFactionname(), result.getAccepted(), result.getTimestamp()) : null;
        });
    }

    public CompletableFuture<Boolean> joinRequestExists(String factionName, Player player) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(FACTIONINVITES, FACTIONINVITES.FACTIONNAME.eq(factionName),
                FACTIONINVITES.UUID.eq(player.getUniqueId().toString()), FACTIONINVITES.TYPE.eq("incoming")));
    }

    public CompletableFuture<List<InviteData>> getInvitesOfType(String factionName, String type) {
        return CompletableFuture.supplyAsync(() -> {
            Result<FactioninvitesRecord> results = ctx.selectFrom(FACTIONINVITES)
                    .where(FACTIONINVITES.FACTIONNAME.eq(factionName), FACTIONINVITES.TYPE.eq(type))
                    .orderBy(FACTIONINVITES.TIMESTAMP.desc())
                    .fetch();

            List<InviteData> data = new ArrayList<>();
            for (FactioninvitesRecord record : results) {
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
            Result<FactioninvitesRecord> results = ctx.selectFrom(FACTIONINVITES)
                    .where(FACTIONINVITES.UUID.eq(player.getUniqueId().toString()), FACTIONINVITES.TYPE.eq("outgoing"))
                    .orderBy(FACTIONINVITES.TIMESTAMP.desc())
                    .fetch();

            List<InviteData> data = new ArrayList<>();
            for (FactioninvitesRecord record : results) {
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

    public CompletableFuture<Void> acceptJoinRequest(UUID playerUUID, String factionName) {
        return CompletableFuture.runAsync(() -> {
            ctx.update(FACTIONINVITES)
                    .set(FACTIONINVITES.ACCEPTED, true)
                    .where(FACTIONINVITES.FACTIONNAME.eq(factionName), FACTIONINVITES.UUID.eq(playerUUID.toString()), FACTIONINVITES.TYPE.eq("incoming"))
                    .execute();
        });
    }

    public CompletableFuture<Void> revokeInvite(UUID playerUUID, String factionName, String type) {
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(FACTIONINVITES)
                    .where(FACTIONINVITES.FACTIONNAME.eq(factionName), FACTIONINVITES.UUID.eq(playerUUID.toString()), FACTIONINVITES.TYPE.eq(type))
                    .execute();
        });
    }

}
