package net.skullian.skyfactions.database.impl.faction;

import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.database.tables.records.FactioninvitesRecord;
import net.skullian.skyfactions.faction.JoinRequestData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Factioninvites.FACTIONINVITES;

public class FactionInvitesDatabaseManager {

    private final DSLContext ctx;

    public FactionInvitesDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    // ------------------ INVITES  ------------------ //

    public CompletableFuture<Void> createFactionInvites(List<InviteData> invites) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (InviteData invite : invites) {
                    trx.dsl().insertInto(FACTIONINVITES)
                            .columns(FACTIONINVITES.FACTIONNAME, FACTIONINVITES.UUID, FACTIONINVITES.INVITER, FACTIONINVITES.TYPE, FACTIONINVITES.TIMESTAMP)
                            .values(invite.getFactionName(), invite.getPlayer().getUniqueId().toString(), (invite.getInviter() != null ? invite.getInviter().getUniqueId().toString() : ""), invite.getType(), invite.getTimestamp())
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<JoinRequestData> getPlayerOutgoingJoinRequest(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            FactioninvitesRecord result = ctx.selectFrom(FACTIONINVITES)
                    .where(FACTIONINVITES.UUID.eq(player.getUniqueId().toString()), FACTIONINVITES.TYPE.eq("incoming"))
                    .fetchOne();

            return result != null ? new JoinRequestData(result.getFactionname(), true, result.getTimestamp()) : null;
        });
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

    public CompletableFuture<Void> removeInvites(List<InviteData> invites) {
        return CompletableFuture.runAsync(() -> {
            ctx.transaction((Configuration trx) -> {
                for (InviteData invite : invites) {
                    trx.dsl().deleteFrom(FACTIONINVITES)
                            .where(FACTIONINVITES.FACTIONNAME.eq(invite.getFactionName()), FACTIONINVITES.UUID.eq(invite.getPlayer().getUniqueId().toString()), FACTIONINVITES.TYPE.eq(invite.getType()))
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Boolean> hasJoinRequest(UUID playerUUID) {
        return CompletableFuture.supplyAsync(() -> ctx.fetchExists(FACTIONINVITES, FACTIONINVITES.UUID.eq(playerUUID.toString()), FACTIONINVITES.TYPE.eq("incoming")));
    }

}
