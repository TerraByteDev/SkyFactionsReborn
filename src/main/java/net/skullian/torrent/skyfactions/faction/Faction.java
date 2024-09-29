package net.skullian.torrent.skyfactions.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.api.NotificationAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.db.AuditLogData;
import net.skullian.torrent.skyfactions.db.InviteData;
import net.skullian.torrent.skyfactions.island.FactionIsland;
import net.skullian.torrent.skyfactions.notification.NotificationType;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public class Faction {

    private FactionIsland island;
    private String name;
    private int last_raid;
    private int level;

    /**
     * Get the Faction's island.
     *
     * @return {@link FactionIsland}
     **/
    public FactionIsland getIsland() {
        return SkyFactionsReborn.db.getFactionIsland(name).join();
    }

    /**
     * Update the name of the faction.
     *
     * @param newName New name of the faction.
     **/
    public void updateName(String newName) {
        SkyFactionsReborn.db.updateFactionName(newName, name).join();
    }

    /**
     * Get the owner of the faction.
     *
     * @return {@link OfflinePlayer}
     */
    public OfflinePlayer getOwner() {
        return SkyFactionsReborn.db.getFactionOwner(name).join();
    }

    public boolean isOwner(Player player) {
        return getOwner().getUniqueId().equals(player.getUniqueId());
    }

    /**
     * Get the admins of the faction.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getAdmins() {
        return SkyFactionsReborn.db.getMembersByRank(name, "admin").join();
    }

    public boolean isAdmin(Player player) {
        return getAdmins().contains(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    /**
     * Change the rank of a player.
     *
     * @param player  Player in question.
     * @param newRank {@link RankType} New Rank of the player.
     */
    public void modifyPlayerRank(OfflinePlayer player, RankType newRank) {
        SkyFactionsReborn.db.updateMemberRank(name, player.getPlayer(), newRank.getRankValue()).join();
    }

    /**
     * Get the moderators of the faction.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getModerators() {
        return SkyFactionsReborn.db.getMembersByRank(name, "moderator").join();
    }

    public boolean isModerator(Player player) {
        return getModerators().contains(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    /**
     * Get the fighters of the faction.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getFighters() {
        return SkyFactionsReborn.db.getMembersByRank(name, "fighter").join();
    }

    public boolean isFighter(Player player) {
        return getFighters().contains(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    /**
     * Get the members of the faction. Does not include moderators & owner.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getMembers() {
        return SkyFactionsReborn.db.getMembersByRank(name, "member").join();
    }

    public boolean isMember(Player player) {
        return getMembers().contains(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    /**
     * Get the total member count of the faction. Used for the Obelisk overview UI.
     *
     * @return {@link Integer}
     */
    public int getTotalMemberCount() {
        int total = getMembers().size() + getModerators().size() + 1;
        return total;
    }

    /**
     * Get all the members in the Faction.
     *
     * @return {@link Integer}
     */
    public List<OfflinePlayer> getAllMembers() {
        List<OfflinePlayer> allMembers = new ArrayList<>();
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
        return SkyFactionsReborn.db.getMOTD(name).join();
    }

    /**
     * Set the Faction's MOTD.
     *
     * @param MOTD New MOTD.
     * @return {@link String}
     */
    public void updateMOTD(String MOTD, Player actor) {
        createAuditLog(actor.getUniqueId(), AuditLogType.MOTD_UPDATE, "%player_name%", actor.getName(), "%new_motd%", MOTD);
        SkyFactionsReborn.db.setMOTD(name, MOTD).join();
    }

    /**
     * Broadcast a message to the Faction's online members.
     *
     * @param message Message to broadcast [{@link String}]
     */
    public void createBroadcast(Player broadcaster, String message) {
        String formatted = TextUtility.color(message).replace("%broadcaster%", broadcaster.getName());
        List<OfflinePlayer> players = getAllMembers();
        for (OfflinePlayer player : players) {
            if (player.isOnline()) {
                String model = Messages.FACTION_BROADCAST_MODEL.get("%broadcaster%", broadcaster.getName(), "%broadcast%", formatted);
                player.getPlayer().sendMessage(model);
            }
        }
    }

    /**
     * Get the Faction's rune count.
     *
     * @return {@link Integer}
     */
    public int getRunes() {
        return SkyFactionsReborn.db.getRunes(getName()).join();
    }

    /**
     * Add to the Faction's rune count.
     *
     * @param addition Amount of runes to add [{@link Integer}]
     */
    public void addRunes(int addition) {
        SkyFactionsReborn.db.addRunes(name, addition).join();
    }

    /**
     * Get the Faction's gem count.
     *
     * @return {@link Integer}
     */
    public int getGems() {
        return SkyFactionsReborn.db.getGems(name).join();
    }

    /**
     * Add gems to the Faction's gem balance.
     *
     * @param addition Gems to add [{@link Integer}]
     */
    public void addGems(int addition) {
        SkyFactionsReborn.db.addGems(name, addition).join();
    }

    /**
     * Kick a player from the Faction for a specific reason.
     *
     * @param player Player to kick [{@link Player}]
     */
    public void kickPlayer(OfflinePlayer player, Player actor) {
        SkyFactionsReborn.db.kickPlayer(player, name).join();
        if (Settings.FACTION_MANAGE_BROADCAST_KICKS.getBoolean()) {
            createBroadcast(actor, Messages.FACTION_MANAGE_KICK_BROADCAST.get("%kicked%", player.getName()));
        }
    }

    /**
     * Ban a player from the faction.
     *
     * @param player Player to ban [{@link Player}]
     */
    public void banPlayer(OfflinePlayer player, Player actor) {
        SkyFactionsReborn.db.banPlayer(name, player).join();
        if (Settings.FACTION_MANAGE_BROADCAST_BANS.getBoolean()) {
            createBroadcast(actor, Messages.FACTION_MANAGE_BAN_BROADCAST.get("%banned%", player.getName()));
        }
    }

    /**
     * Remove a ban from a player.
     *
     * @param player {@link OfflinePlayer}
     */
    public void unbanPlayer(OfflinePlayer player) {
        SkyFactionsReborn.db.unbanPlayer(name, player).join();
    }

    /**
     * @param player Player to check ban status of [{@link Player}]
     * @return {@link Boolean}
     */
    public boolean isPlayerBanned(OfflinePlayer player) {
        return SkyFactionsReborn.db.isPlayerBanned(name, player).join();
    }

    /**
     * Get a list of all the banned players.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getBannedPlayers() {
        return SkyFactionsReborn.db.getBannedPlayers(name).join();
    }

    /**
     * Remove a player from the faction (willingly).
     *
     * @param player Player who is leaving [{@link Player}]
     */
    public void leaveFaction(OfflinePlayer player) {
        SkyFactionsReborn.db.leaveFaction(name, player).join();
    }

    /**
     * Add a new member to the Faction.
     *
     * @param playerUUID UUID of the player to add [{@link Player}]
     */
    public void addFactionMember(UUID playerUUID) {
        SkyFactionsReborn.db.addFactionMember(playerUUID, name).join();
        createBroadcast(Bukkit.getPlayer(playerUUID).getPlayer(), "player joined the faction temporary message");
    }

    /**
     * Get all audit logs (player join, kick, leave, ban, etc.) of the Faction.
     *
     * @return {@link List<AuditLogData>}
     */
    public List<AuditLogData> getAuditLogs() {
        return SkyFactionsReborn.db.getAuditLogs(name).join();
    }

    /**
     * Create an audit log for the Faction.
     *
     * @param playerUUID   UUID of the player in question [{@link UUID}]
     * @param type         {@link AuditLogType} Type of audit log.
     * @param replacements Values to replace.
     */
    public void createAuditLog(UUID playerUUID, AuditLogType type, Object... replacements) {
        SkyFactionsReborn.db.createAuditLog(playerUUID, type.getTitle(replacements), type.getDescription(replacements), name).join();
    }

    /**
     * Get all incoming join requests to the Faction.
     *
     * @return {@link List<InviteData>}
     */
    public List<InviteData> getJoinRequests() {
        return SkyFactionsReborn.db.getInvitesOfType(name, "incoming").join();
    }

    /**
     * Get all outgoing invites from the Faction.
     *
     * @return {@link List<InviteData>}
     */
    public List<InviteData> getOutgoingInvites() {
        return SkyFactionsReborn.db.getInvitesOfType(name, "outgoing").join();
    }

    /**
     * Invite another player to your faction.
     *
     * @param player Player to invite to the faction [{@link Player}]
     */
    public void createInvite(OfflinePlayer player, Player inviter) {
        SkyFactionsReborn.db.createInvite(player.getUniqueId(), name, "outgoing", inviter).join();
        createAuditLog(player.getUniqueId(), AuditLogType.INVITE_CREATE, "%inviter%", inviter.getName(), "%player_name%", player.getName());

        if (player.isOnline()) {
            Messages.FACTION_INVITE_NOTIFICATION.send(player.getPlayer());
        } else {
            NotificationAPI.createNotification(player.getUniqueId(), NotificationType.INVITE_CREATE, "%player_name%", inviter.getName(), "%faction_name%", name);
        }
    }

    /**
     * Create a join request to this Faction.
     *
     * @param player Player who is requesting to join this faction [{@link Player}]
     */
    public void createJoinRequest(OfflinePlayer player) {
        createAuditLog(player.getUniqueId(), AuditLogType.JOIN_REQUEST_CREATE, "%player_name%", player.getName());
        SkyFactionsReborn.db.createInvite(player.getUniqueId(), name, "incoming", null).join();
        List<OfflinePlayer> users = Stream.concat(getModerators().stream(), getAdmins().stream()).collect(Collectors.toList());
        users.add(getOwner());

        for (OfflinePlayer user : users) {
            if (user.isOnline()) {
                Messages.JOIN_REQUEST_NOTIFICATION.send(user.getPlayer());
            }
        }
    }

    /**
     * Revoke a Faction invite.
     *
     * @param data  Data of the Invite [{@link InviteData}]
     * @param actor Player who revoked the invite [{@link Player}]
     */
    public void revokeInvite(InviteData data, Player actor) {
        createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.INVITE_REVOKE, "%player%", actor.getName(), "%invited%", data.getPlayer().getName());
        SkyFactionsReborn.db.revokeInvite(data.getFactionName(), data.getPlayer().getUniqueId(), "outgoing").join();
    }

    /**
     * Accept a Faction join request.
     *
     * @param data  Data of the Invite [{@link InviteData}]
     * @param actor Player who accepted the invite [{@link Player}]
     */
    public void acceptJoinRequest(InviteData data, Player actor) {
        createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.JOIN_REQUEST_ACCEPT, "%player%", data.getPlayer().getName(), "%inviter%", actor.getName());
        SkyFactionsReborn.db.acceptJoinRequest(name, data.getPlayer().getUniqueId()).join();
    }

    /**
     * Reject a Player's join request to your Faction.
     *
     * @param data  Data of the invite [{@link InviteData}]
     * @param actor Player who rejected the invite [{@link Player}]
     */
    public void rejectJoinRequest(InviteData data, Player actor) {
        createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.JOIN_REQUEST_REJECT, "%faction_player%", actor.getName(), "%player%", data.getPlayer().getName());
        SkyFactionsReborn.db.revokeInvite(name, data.getPlayer().getUniqueId(), "incoming").join();
        NotificationAPI.createNotification(data.getPlayer().getUniqueId(), NotificationType.JOIN_REQUEST_ACCEPT, "%player_name%", actor.getName(), "%faction_name%", name);
    }
}
