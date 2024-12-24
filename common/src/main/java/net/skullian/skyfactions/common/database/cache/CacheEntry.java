package net.skullian.skyfactions.common.database.cache;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.database.struct.AuditLogData;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.faction.RankType;
import net.skullian.skyfactions.common.notification.NotificationData;

import lombok.Getter;
import lombok.Setter;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SkyLocation;
import org.jetbrains.annotations.Nullable;

@Getter
public class CacheEntry {

    private int runes = 0; // Player & Faction
    private int gems = 0; // Player & Faction
    private final List<SkyLocation> defencesToRegister = new ArrayList<>(); // Player * Faction
    private final List<SkyLocation> defencesToRemove = new ArrayList<>(); // Player & Faction
    @Setter private String newLocale; // Player & Faction
    @Setter private boolean shouldRegister; // Player Exclusive
    @Getter private String newDiscordID; // Player Exclusive
    @Getter private long newLastRaid = -1; // Player & Faction

    private final List<NotificationData> notificationsToAdd = new ArrayList<>(); // Player Exclusive
    private final List<NotificationData> notificationsToRemove = new ArrayList<>(); // Player Exclusive

    private final Map<UUID, RankType> newRanks = new HashMap<>(); // Faction Exclusive
    private final List<SkyUser> membersToAdd = new ArrayList<>(); // Faction Exclusive
    private final List<SkyUser> membersToRemove = new ArrayList<>(); // Faction Exclusive
    private final List<SkyUser> membersToBan = new ArrayList<>(); // Faction Exclusive
    private final List<SkyUser> membersToUnban = new ArrayList<>(); // Faction Exclusive
    private final List<InviteData> invitesToCreate = new ArrayList<>(); // Faction Exclusive
    private final List<InviteData> invitesToRemove = new ArrayList<>(); // Faction Exclusive
    private final List<AuditLogData> auditLogsToAdd = new ArrayList<>(); // Faction Exclusive

    public void addRunes(int amount) {
        runes += amount;
    }

    public void removeRunes(int amount) {
        runes -= amount;
    }

    public void addGems(int amount) {
        gems += amount;
    }

    public void removeGems(int amount) {
        gems -= amount;
    }

    public void addDefence(SkyLocation location) {
        defencesToRemove.remove(location);
        defencesToRegister.add(location);
    }

    public void removeDefence(SkyLocation location) {
        defencesToRegister.remove(location);
        defencesToRemove.add(location);
    }

    public void setNewRank(UUID playerUUID, RankType rankType) {
        newRanks.put(playerUUID, rankType);
    }

    public void addMember(SkyUser player) {
        membersToRemove.remove(player);
        membersToAdd.add(player);
    }

    public void removeMember(SkyUser player) {
        membersToAdd.remove(player);
        membersToRemove.add(player);
    }

    public void banMember(SkyUser player) {
        membersToUnban.remove(player);
        membersToBan.add(player);
    }

    public void unbanMember(SkyUser player) {
        membersToBan.remove(player);
        membersToBan.add(player);
    }

    public void createInvite(InviteData inviteData) {
        invitesToRemove.remove(inviteData);
        invitesToCreate.add(inviteData);
    }

    public void removeInvite(InviteData inviteData) {
        invitesToCreate.remove(inviteData);
        invitesToRemove.add(inviteData);
    }

    public void addAuditLog(AuditLogData auditLog) {
        auditLogsToAdd.add(auditLog);
    }

    public void addNotification(NotificationData notification) {
        notificationsToRemove.remove(notification);
        notificationsToAdd.add(notification);
    }

    public void removeNotification(NotificationData notification) {
        SkyApi.getInstance().getNotificationAPI().removeNotification(notification.getUuid(), notification);

        notificationsToAdd.remove(notification);
        notificationsToRemove.add(notification);
    }

    public void setNewDiscordID(UUID playerUUID, String id) {
        this.newDiscordID = id;

        if (SkyApi.getInstance().getPlayerAPI().isPlayerCached(playerUUID)) {
            SkyApi.getInstance().getPlayerAPI().getCachedPlayerData(playerUUID).setDISCORD_ID(id);
        }
    }

    public void setNewLastRaid(UUID playerUUID, long time) {
        this.newLastRaid = time;

        if (SkyApi.getInstance().getPlayerAPI().isPlayerCached(playerUUID)) {
            SkyApi.getInstance().getPlayerAPI().getCachedPlayerData(playerUUID).setLAST_RAID(time);
        }
    }

    public void onIslandRemove() {
        this.runes = 0;
        this.gems = 0;
        this.defencesToRegister.clear();
        this.defencesToRemove.clear();
        this.notificationsToAdd.clear();
        this.notificationsToRemove.clear();
    }

    /**
     *
     * @param toCache - UUID of player to cache (only used when the entry is for a player)
     * @param factionName - explanatory. same reason as above.
     * @return {@link CompletableFuture<Void>}
     */
    public CompletableFuture<Void> cache(@Nullable String toCache, @Nullable String factionName) {
        if (SkyApi.getInstance().getDatabaseManager().closed) {
            return CompletableFuture.completedFuture(null);
        }
        if (factionName != null) {
            return CompletableFuture.allOf(
                    SkyApi.getInstance().getDatabaseManager().getCurrencyManager().modifyGems(factionName, gems, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set gems of faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getCurrencyManager().modifyRunes(factionName, runes, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set runes of faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getDefencesManager().registerDefenceLocations(defencesToRegister, factionName, true).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register new defences for faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getDefencesManager().removeDefenceLocations(defencesToRemove, factionName, true).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove defences for faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionsManager().updateFactionLocale(factionName, newLocale).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update locale for faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionsManager().updateFactionMemberRanks(factionName, newRanks).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update ranks for faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionsManager().addFactionMembers(membersToAdd, factionName).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to add players to faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionsManager().removeMembers(membersToRemove, factionName).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to kick players from faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionsManager().banMembers(membersToBan, factionName).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to ban players from faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionsManager().unbanMembers(membersToUnban, factionName).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to unban players from faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionInvitesManager().createFactionInvites(invitesToCreate).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to create invites for faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionInvitesManager().removeInvites(invitesToRemove).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove invites for faction " + factionName, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getFactionAuditLogManager().createAuditLogs(auditLogsToAdd).exceptionally((ex -> {
                        throw new RuntimeException("Failed to create audit logs for faction " + factionName, ex);
                    })),
                    SkyApi.getInstance().getDatabaseManager().getFactionIslandManager().updateFactionLastRaid(factionName, newLastRaid).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update last raid for faction " + factionName, ex);
                    })
            );
        } else {
            UUID uuid = UUID.fromString(Objects.requireNonNull(toCache));
            return CompletableFuture.allOf(
                    SkyApi.getInstance().getDatabaseManager().getPlayerManager().registerPlayer(uuid, shouldRegister).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getCurrencyManager().modifyGems(uuid, gems, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set gems of player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getCurrencyManager().modifyRunes(uuid, runes, false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to set runes of player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getDefencesManager().registerDefenceLocations(defencesToRegister, uuid.toString(), false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to register new defences for player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getDefencesManager().registerDefenceLocations(defencesToRemove, uuid.toString(), false).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove defences for player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getPlayerManager().setPlayerLocale(uuid, newLocale).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update defences for player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getNotificationManager().createNotifications(notificationsToAdd).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to create notifications for player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getNotificationManager().removeNotifications(notificationsToRemove).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to remove notifications for player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getPlayerManager().registerDiscordLink(uuid, newDiscordID).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update discord link for player " + uuid, ex);
                    }),
                    SkyApi.getInstance().getDatabaseManager().getPlayerManager().updateLastRaid(uuid, newLastRaid).exceptionally((ex) -> {
                        throw new RuntimeException("Failed to update last raid for player " + uuid, ex);
                    })
            );
        }
    }
}
