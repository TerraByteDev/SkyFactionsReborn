package net.skullian.torrent.skyfactions.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.db.AuditLogData;
import net.skullian.torrent.skyfactions.db.InviteData;
import net.skullian.torrent.skyfactions.island.FactionIsland;
import net.skullian.torrent.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class Faction {

    private FactionIsland island;
    private String name;
    private int last_raid;
    private int level;

    /**
        Get the Faction's island.

        @return {@link FactionIsland}
    **/
    public FactionIsland getIsland() {
        return SkyFactionsReborn.db.getFactionIsland(name).join();
    }

    /**
        Update the name of the faction.

        @param newName New name of the faction.
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

    public boolean isOwner(Player player) { return getOwner().getUniqueId().equals(player.getUniqueId()); }

    /**
     * Get the admins of the faction.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getAdmins() {
        return SkyFactionsReborn.db.getMembersByRank(name, "admin").join();
    }

    public boolean isAdmin(Player player) { return getAdmins().contains(Bukkit.getOfflinePlayer(player.getUniqueId())); }

    /**
     * Change the rank of a player.
     *
     * @param player Player in question.
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

    public boolean isModerator(Player player) { return getModerators().contains(Bukkit.getOfflinePlayer(player.getUniqueId())); }

    /**
     * Get the fighters of the faction.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getFighters() {
        return SkyFactionsReborn.db.getMembersByRank(name, "fighter").join();
    }

    public boolean isFighter(Player player) { return getFighters().contains(Bukkit.getOfflinePlayer(player.getUniqueId())); }

    /**
     * Get the members of the faction. Does not include moderators & owner.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getMembers() {
        return SkyFactionsReborn.db.getMembersByRank(name, "member").join();
    }

    public boolean isMember(Player player) { return getMembers().contains(Bukkit.getOfflinePlayer(player.getUniqueId())); }

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
    public void updateMOTD(String MOTD) {
        SkyFactionsReborn.db.setMOTD(name, MOTD).join();
    }

    /**
     * Broadcast a message to the Faction's online members.
     *
     * @param message Message to broadcast.
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
    public int getRunes() { return SkyFactionsReborn.db.getRunes(getName()).join(); }

    /**
     * Add to the Faction's rune count.
     *
     * @param addition Amount of runes to add.
     */
    public void addRunes(int addition) { SkyFactionsReborn.db.addRunes(name, addition).join(); }

    /**
     * Get the Faction's gem count.
     *
     * @return {@link Integer}
     */
    public int getGems() { return SkyFactionsReborn.db.getGems(name).join(); }

    /**
     * Add gems to the Faction's gem balance.
     *
     * @param addition Gems to add.
     */
    public void addGems(int addition) { SkyFactionsReborn.db.addGems(name, addition).join(); }

    /**
     * Kick a player from the Faction for a specific reason.
     *
     * @param player Player to kick.
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
     * @param player Player to ban.
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
     *
     * @param player Player to check ban status of.
     * @return {@link Boolean}
     */
    public boolean isPlayerBanned(OfflinePlayer player) {
        return SkyFactionsReborn.db.isPlayerBanned(name, player).join();
    }

    /**
     * Get a list of all the banned players.
     * @return {@link List<OfflinePlayer>}
     */
    public List<OfflinePlayer> getBannedPlayers() {
        return SkyFactionsReborn.db.getBannedPlayers(name).join();
    }

    /**
     * Remove a player from the faction (willingly).
     *
     * @param player Player who is leaving.
     */
    public void leaveFaction(OfflinePlayer player) {
        SkyFactionsReborn.db.leaveFaction(name, player).join();
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
     * @param player Player in question.
     * @param type {@link AuditLogType} Type of audit log.
     * @param replacements Values to replace. (e.g. '%player_name%, player.getName()').
     */
    public void createAuditLog(OfflinePlayer player, AuditLogType type, Object... replacements) {
        SkyFactionsReborn.db.createAuditLog(player, type.getTitle(replacements), type.getDescription(replacements), name).join();
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
     * @param player Player to invite to the faction.
     */
    public void createInvite(OfflinePlayer player) {
        SkyFactionsReborn.db.createInvite(player.getPlayer(), name, "outgoing").join();
    }
}
