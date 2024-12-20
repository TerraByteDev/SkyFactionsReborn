package net.skullian.skyfactions.common.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.common.api.InvitesAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.struct.AuditLogData;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.island.impl.FactionIsland;
import net.skullian.skyfactions.common.notification.NotificationType;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.text.TextUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public class Faction {

    private FactionIsland island;
    private String name;
    private long lastRaid;
    private int level;
    private SkyUser owner;
    private List<SkyUser> admins;
    private List<SkyUser> moderators;
    private List<SkyUser> fighters;
    private List<SkyUser> members;
    private String motd;
    public int runes;
    public int gems;
    public String locale;
    private boolean electionRunning;
    public List<SkyUser> bannedPlayers;
    public long lastRenamed;
    public List<InviteData> invites;
    public List<AuditLogData> auditLogs;

    public int getRunes() {
        if (SkyApi.getInstance().getCacheService().getFactionsToCache().containsKey(getName()))
            return (runes += SkyApi.getInstance().getCacheService().getFactionsToCache().get(getName()).getRunes());
        else return runes;
    }

    public int getGems() {
        if (SkyApi.getInstance().getCacheService().getFactionsToCache().containsKey(getName()))
            return (gems += SkyApi.getInstance().getCacheService().getFactionsToCache().get(getName()).getGems());
        else return gems;
    }

    /**
     * Update the name of the faction.
     *
     * @param newName New name of the faction.
     **/
    public void updateName(String newName) {
        SkyApi.getInstance().getFactionAPI().onFactionRename(getName(), newName);

        SkyApi.getInstance().getPlayerAPI().getOnlinePlayers().stream()
                        .filter(player -> player.isOnline() && player.hasMetadata("inFacRelatedUI"))
                        .forEach(SkyUser::closeInventory);

        this.name = newName;
        SkyApi.getInstance().getDatabaseManager().getFactionsManager().updateFactionName(getName(), newName);
    }

    /**
     * Change the rank of a player.
     *
     * @param player  Player in question.
     * @param newRank {@link RankType} New Rank of the player.
     */
    public void modifyPlayerRank(SkyUser player, RankType newRank, SkyUser actor) {
        RankType oldRank = getRankType(player.getUniqueId());
        SkyApi.getInstance().getCacheService().getEntry(this).setNewRank(player.getUniqueId(), newRank);
        cache(player, oldRank.getRankValue(), newRank);
        SkyApi.getInstance().getNotificationAPI().createNotification(player.getUniqueId(), NotificationType.RANK_UPDATED, "new_rank", getRank(player.getUniqueId()), "player_name", actor.getName());
    }

    /**
     * Get all the members in the Faction.
     *
     * @return {@link Integer}
     */
    public List<SkyUser> getAllMembers() {
        List<SkyUser> allMembers = new ArrayList<>();
        allMembers.addAll(getMembers());
        allMembers.addAll(getFighters());
        allMembers.addAll(getModerators());
        allMembers.addAll(getAdmins());
        allMembers.add(getOwner());

        return allMembers;
    }

    /**
     * Get the Faction's MOTD.
     *
     * @return {@link String}
     */
    public String getMOTD() {
        return motd;
    }

    /**
     * Set the Faction's MOTD.
     *
     * @param MOTD New MOTD.
     * @return {@link String}
     */
    public CompletableFuture<Void> updateMOTD(String MOTD, SkyUser actor) {
        this.motd = MOTD;
        createAuditLog(actor.getUniqueId(), AuditLogType.MOTD_UPDATE, "player_name", actor.getName(), "new_motd", MOTD);
        return SkyApi.getInstance().getDatabaseManager().getFactionsManager().updateFactionMOTD(name, MOTD);
    }

    /**
     * Broadcast a message to the Faction's online members.
     *
     * @param message Message to broadcast [{@link Messages}]
     */
    public void createBroadcast(SkyUser broadcaster, Messages message, Object... replacements) {
        List<SkyUser> players = getAllMembers();
        for (SkyUser player : players) {
            if (player.isOnline()) {
                String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

                Component model = TextUtility.fromList(Messages.FACTION_BROADCAST_MODEL.getStringList(locale), locale, player, "broadcaster", broadcaster.getName(), "broadcast",
                        Messages.replace(message.getString(locale), player, replacements)
                );

                player.sendMessage(model);
            }
        }
    }

    /**
     * Broadcast a message to the Faction's online members.
     *
     * @param message Message to broadcast [{@link String}]
     */
    public void createBroadcast(SkyUser broadcaster, String message) {
        List<SkyUser> players = getAllMembers();
        for (SkyUser player : players) {
            if (player.isOnline()) {
                String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());

                Component model = TextUtility.fromList(Messages.FACTION_BROADCAST_MODEL.getStringList(locale), locale, player, "broadcaster", broadcaster.getName(), "broadcast",
                        Messages.replace(message, player));

                player.sendMessage(model);
            }
        }
    }

    /**
     * Add to the Faction's rune count.
     *
     * @param addition Amount of runes to add [{@link Integer}]
     */
    public void addRunes(int addition) {
        runes += addition;
        SkyApi.getInstance().getCacheService().getEntry(this).addRunes(addition);
    }

    /**
     * Remove runes from a Faction.
     *
     * @param subtraction Amount of runes to remove [{@link Integer}]
     */
    public void subtractRunes(int subtraction) {
        runes -= subtraction;
        SkyApi.getInstance().getCacheService().getEntry(this).removeRunes(subtraction);
    }

    /**
     * Add gems to the Faction's gem balance.
     *
     * @param addition Gems to add [{@link Integer}]
     */
    public void addGems(int addition) {
        gems += addition;
        SkyApi.getInstance().getCacheService().getEntry(this).addGems(addition);
    }

    /**
     * Removes gems to the Factions' gem balance.
     *
     * @param subtraction Gems to remove [{@link Integer}]
     */
    public void subtractGems(int subtraction) {
        gems -= subtraction;
        SkyApi.getInstance().getCacheService().getEntry(this).removeGems(subtraction);
    }

    public void updateLocale(String newLocale) {
        SkyApi.getInstance().getCacheService().getEntry(this).setNewLocale(newLocale);
    }

    /**
     * Kick a player from the Faction for a specific reason.
     *
     * @param player Player to kick [{@link SkyUser}]
     */
    public void kickPlayer(SkyUser player, SkyUser actor) {
        SkyApi.getInstance().getCacheService().getEntry(this).removeMember(player);
        if (Settings.FACTION_MANAGE_BROADCAST_KICKS.getBoolean()) {
            createBroadcast(actor, Messages.FACTION_MANAGE_KICK_BROADCAST, "<kicked>", player.getName());
        }
    }

    /**
     * Ban a player from the faction.
     *
     * @param player Player to ban [{@link SkyUser}]
     */
    public void banPlayer(SkyUser player, SkyUser actor) {
        SkyApi.getInstance().getCacheService().getEntry(this).banMember(player);
        bannedPlayers.add(player);
        createAuditLog(player.getUniqueId(), AuditLogType.PLAYER_BAN, "banned", player.getName(), "player", actor.getName());
        if (Settings.FACTION_MANAGE_BROADCAST_BANS.getBoolean()) {
            createBroadcast(actor, Messages.FACTION_MANAGE_BAN_BROADCAST, "<banned>", player.getName());
        }
    }

    /**
     * Remove a ban from a player.
     *
     * @param player {@link SkyUser}
     */
    public void unbanPlayer(SkyUser player) {
        bannedPlayers.remove(player);
        SkyApi.getInstance().getCacheService().getEntry(this).unbanMember(player); // todo audit log & ban viewing
    }

    /**
     * @param player Player to check ban status of [{@link SkyUser}]
     * @return {@link Boolean}
     */
    public boolean isPlayerBanned(SkyUser player) {
        return bannedPlayers.contains(player);
    }

    /**
     * Get a list of all the banned players.
     *
     * @return {@link List<SkyUser>}
     */
    public CompletableFuture<List<SkyUser>> getBannedPlayers() {
        return SkyApi.getInstance().getDatabaseManager().getFactionsManager().getBannedPlayers(name);
    }

    /**
     * Remove a player from the faction (willingly).
     *
     * @param player Player who is leaving [{@link SkyUser}]
     */
    public void leaveFaction(SkyUser player) {
        removeFromFaction(player);
        SkyApi.getInstance().getCacheService().getEntry(this).removeMember(player);
    }

    /**
     * Add a new member to the Faction.
     *
     * @param player UUID of the player to add [{@link SkyUser}]
     */
    public void addFactionMember(SkyUser player) {
        SkyApi.getInstance().getCacheService().getEntry(this).addMember(player);
        members.add(player);
        createAuditLog(player.getUniqueId(), AuditLogType.PLAYER_JOIN, "player_name", player.getName());
        createBroadcast(player, Messages.FACTION_JOIN_BROADCAST, "<player_name>", player.getName());

        SkyApi.getInstance().getFactionAPI().addMemberToRegion(player, this);
    }

    /**
     * Create an audit log for the Faction.
     *
     * @param playerUUID   UUID of the player in question [{@link UUID}]
     * @param type         {@link AuditLogType} Type of audit log.
     * @param replacements Values to replace.
     */
    public void createAuditLog(UUID playerUUID, AuditLogType type, Object... replacements) {
        AuditLogData auditLogData = createData(playerUUID, type, replacements);
        auditLogs.add(auditLogData);
        SkyApi.getInstance().getCacheService().getEntry(this).addAuditLog(auditLogData);
    }

    private AuditLogData createData(UUID playerUUID, AuditLogType type, Object... replacements) {
        return new AuditLogData(
                SkyApi.getInstance().getUserManager().getUser(playerUUID),
                getName(),
                type.name(),
                replacements,
                System.currentTimeMillis()
        );
    }

    /**
     * Get all incoming join requests to the Faction.
     *
     * @return {@link List<InviteData>}
     */
    public List<InviteData> getJoinRequests() {
        return invites.stream()
                .filter(invite -> invite.getType().equals("incoming"))
                .collect(Collectors.toList());
    }

    /**
     * Get all outgoing invites from the Faction.
     *
     * @return {@link List<InviteData>}
     */
    public List<InviteData> getOutgoingInvites() {
        return invites.stream()
                .filter(invite -> invite.getType().equals("outgoing"))
                .collect(Collectors.toList());
    }

    public JoinRequestData getPlayerJoinRequest(SkyUser player) {
        InviteData data = invites.stream()
                .filter(invite -> invite.getPlayer().getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);

        return data != null ? new JoinRequestData(
                data.getFactionName(),
                false,
                data.getTimestamp()
        ) : null;
    }

    /**
     * Invite another player to your faction.
     *
     * @param data Data of the invite [{@link InviteData}]
     */
    public void createInvite(InviteData data) {
        SkyUser player = data.getPlayer();
        SkyUser inviter = data.getInviter();

        InvitesAPI.onInviteCreate(player.getUniqueId(), data);
        invites.add(data);
        SkyApi.getInstance().getCacheService().getEntry(this).createInvite(data);
        createAuditLog(player.getUniqueId(), AuditLogType.INVITE_CREATE, "inviter", inviter.getName(), "player_name", player.getName());
        SkyApi.getInstance().getNotificationAPI().createNotification(player.getUniqueId(), NotificationType.INVITE_CREATE, "player_name", inviter.getName(), "faction_name", name);

        if (player.isOnline()) {
            Messages.FACTION_INVITE_NOTIFICATION.send(player, SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()));
        }
    }

    /**
     * Create a join request to this Faction.
     *
     * @param data Data of the join request [{@link InviteData}]
     */
    public void createJoinRequest(InviteData data) {
        SkyUser player = data.getPlayer();

        invites.add(data);
        SkyApi.getInstance().getCacheService().getEntry(this).createInvite(data);
        createAuditLog(player.getUniqueId(), AuditLogType.JOIN_REQUEST_CREATE, "player_name", player.getName());

        List<SkyUser> users = Stream.concat(getModerators().stream(), getAdmins().stream()).collect(Collectors.toList());
        users.add(getOwner());

        for (SkyUser user : users) {
            if (user.isOnline()) {
                Messages.JOIN_REQUEST_NOTIFICATION.send(user, SkyApi.getInstance().getPlayerAPI().getLocale(user.getUniqueId()));
            }
        }
    }

    public boolean isOwner(SkyUser player) {
        return getOwner().equals(player);
    }

    public boolean isModerator(SkyUser player) {
        return getModerators().contains(player);
    }

    public boolean isAdmin(SkyUser player) {
        return getAdmins().contains(player);
    }

    /**
     * Revoke a Faction invite.
     *
     * @param data  Data of the Invite [{@link InviteData}]
     * @param type Type of the audit log to be created [{@link AuditLogType}]
     * @param replacements Values to replace in the audit log.
     */
    public void revokeInvite(InviteData data, AuditLogType type, Object... replacements) {
        SkyUser player = data.getPlayer();

        invites.remove(data);
        SkyApi.getInstance().getCacheService().getEntry(this).removeInvite(data);
        InvitesAPI.onInviteRemove(player.getUniqueId(), data);
        createAuditLog(player.getUniqueId(), type, replacements);
    }

    /**
     * Get the total amount of members in the Faction.
     *
     * @return Amount of members {@link Integer}
     */
    public int getTotalMemberCount() {
        return getMembers().size() + getFighters().size() + getModerators().size() + getAdmins().size() + 1;
    }

    /**
     * Reject a Player's join request to your Faction.
     *
     * @param data  Data of the invite [{@link InviteData}]
     * @param actor Player who rejected the invite [{@link SkyUser}]
     */
    public void rejectJoinRequest(InviteData data, SkyUser actor) {
        invites.remove(data);
        SkyApi.getInstance().getCacheService().getEntry(this).removeInvite(data);

        createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.JOIN_REQUEST_REJECT, "faction_player", actor.getName(), "player", data.getPlayer().getName());
        SkyApi.getInstance().getNotificationAPI().createNotification(data.getPlayer().getUniqueId(), NotificationType.JOIN_REQUEST_ACCEPT, "player_name", actor.getName(), "faction_name", name);
    }

    public InviteData toInviteData(JoinRequestData data, SkyUser player) {
        return new InviteData(
                player,
                null,
                data.getFactionName(),
                "incoming",
                data.getTimestamp()
        );
    }

    /**
     * Get the configured rank title of a member.
     *
     * @param playerUUID UUID of the player {@link UUID}
     * @return The rank of the player. {@link String}
     */
    public String getRank(UUID playerUUID) {
        SkyUser player = SkyApi.getInstance().getUserManager().getUser(playerUUID);
        String locale = player.isOnline() ? SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId()) : Messages.getDefaulLocale();

        if (owner.equals(player)) return Messages.FACTION_OWNER_TITLE.getString(locale);
        if (admins.contains(player)) return Messages.FACTION_ADMIN_TITLE.getString(locale);
        if (moderators.contains(player)) return Messages.FACTION_MODERATOR_TITLE.getString(locale);
        if (fighters.contains(player)) return Messages.FACTION_FIGHTER_TITLE.getString(locale);
        if (members.contains(player)) return Messages.FACTION_MEMBER_TITLE.getString(locale);

        return "<red>N/A";
    }

    public RankType getRankType(UUID playerUUID) {
        SkyUser player = SkyApi.getInstance().getUserManager().getUser(playerUUID);

        if (owner.equals(player)) return RankType.OWNER;
        if (admins.contains(player)) return RankType.ADMIN;
        if (moderators.contains(player)) return RankType.MODERATOR;
        if (fighters.contains(player)) return RankType.FIGHTER;
        if (members.contains(player)) return RankType.MEMBER;

        return null;
    }

    public boolean isInFaction(UUID playerUUID) {
        return getAllMembers().stream()
                .anyMatch(player -> player.getUniqueId().equals(playerUUID));
    }

    private void cache(SkyUser player, String oldRank, RankType newType) {
        switch (oldRank) {
            case "admin":
                admins.remove(player);
                break;
            case "moderator":
                moderators.remove(player);
                break;
            case "fighter":
                fighters.remove(player);
                break;
            case "member":
                members.remove(player);
                break;
        }

        switch (newType.getRankValue()) {
            case "admin":
                admins.add(player);
                break;
            case "moderator":
                moderators.add(player);
                break;
            case "fighter":
                fighters.add(player);
                break;
            case "members":
                members.add(player);
                break;
        }
    }

    private void removeFromFaction(SkyUser player) {
        SkyApi.getInstance().getFactionAPI().removeMemberFromRegion(player, this);

        SkyApi.getInstance().getFactionAPI().getFactionUserCache().remove(player.getUniqueId());
        if (owner.equals(player)) {
            owner = null;
        } else {
            admins.remove(player);
            moderators.remove(player);
            fighters.remove(player);
            members.remove(player);
        }
    }
}
