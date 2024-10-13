package net.skullian.skyfactions.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.db.AuditLogData;
import net.skullian.skyfactions.db.InviteData;
import net.skullian.skyfactions.island.FactionIsland;
import net.skullian.skyfactions.notification.NotificationType;
import net.skullian.skyfactions.util.ErrorHandler;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
    private int last_raid;
    private int level;
    private OfflinePlayer owner;
    private List<OfflinePlayer> admins;
    private List<OfflinePlayer> moderators;
    private List<OfflinePlayer> fighters;
    private List<OfflinePlayer> members;
    private String motd;
    private int runes;
    private int gems;

    /**
     * Update the name of the faction.
     *
     * @param newName New name of the faction.
     **/
    public CompletableFuture<Void> updateName(String newName) {
        name = newName;
        return SkyFactionsReborn.databaseHandler.updateFactionName(newName, name);
    }

    /**
     * Change the rank of a player.
     *
     * @param player  Player in question.
     * @param newRank {@link RankType} New Rank of the player.
     */
    public CompletableFuture<String> modifyPlayerRank(OfflinePlayer player, RankType newRank) {
        return SkyFactionsReborn.databaseHandler.updateMemberRank(name, player.getPlayer(), newRank.getRankValue()).thenApply((oldRank) -> {
            cache(player, oldRank, newRank);
            return newRank.getRankValue(); // or some other string value
        });
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
        return motd;
    }

    /**
     * Set the Faction's MOTD.
     *
     * @param MOTD New MOTD.
     * @return {@link String}
     */
    public CompletableFuture<Void> updateMOTD(String MOTD, Player actor) {
        motd = MOTD;
        return CompletableFuture.allOf(createAuditLog(actor.getUniqueId(), AuditLogType.MOTD_UPDATE, "%player_name%", actor.getName(), "%new_motd%", MOTD), SkyFactionsReborn.databaseHandler.setMOTD(name, MOTD));
    }

    /**
     * Broadcast a message to the Faction's online members.
     *
     * @param message Message to broadcast [{@link String}]
     */
    public void createBroadcast(OfflinePlayer broadcaster, String message) {
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
     * Add to the Faction's rune count.
     *
     * @param addition Amount of runes to add [{@link Integer}]
     */
    public CompletableFuture<Void> addRunes(int addition) {
        return SkyFactionsReborn.databaseHandler.addRunes(name, addition).whenComplete((ignored, ex) -> {
            if (ex != null) {
                throw new RuntimeException(ex);
            }

            runes += addition;
        });
    }

    /**
     * Remove runes from a Faction.
     *
     * @param subtraction Amount of runes to remove [{@link Integer}]
     */
    public CompletableFuture<Void> subtractRunes(int subtraction) {
        return SkyFactionsReborn.databaseHandler.removeRunes(name, subtraction).whenComplete((ignored, ex) -> {
            if (ex != null) {
                throw new RuntimeException(ex);
            }

            runes -= subtraction;
        });
    }

    /**
     * Add gems to the Faction's gem balance.
     *
     * @param addition Gems to add [{@link Integer}]
     */
    public CompletableFuture<Void> addGems(int addition) {
        gems += addition;
        return SkyFactionsReborn.databaseHandler.addGems(name, addition).exceptionally((ex) -> {
            ex.printStackTrace();
            gems -= addition;
            return null;
        });
    }

    /**
     * Kick a player from the Faction for a specific reason.
     *
     * @param player Player to kick [{@link Player}]
     */
    public CompletableFuture<Void> kickPlayer(OfflinePlayer player, Player actor) {
        return SkyFactionsReborn.databaseHandler.kickPlayer(player, name).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(actor, "kick a member from the Faction", "SQL_FACTION_KICK", ex);
                return;
            }
            if (Settings.FACTION_MANAGE_BROADCAST_KICKS.getBoolean()) {
                createBroadcast(actor, Messages.FACTION_MANAGE_KICK_BROADCAST.get("%kicked%", player.getName()));
            }
        });
    }

    /**
     * Ban a player from the faction.
     *
     * @param player Player to ban [{@link Player}]
     */
    public CompletableFuture<Void> banPlayer(OfflinePlayer player, Player actor) {
        return SkyFactionsReborn.databaseHandler.banPlayer(name, player).whenComplete((ignored, ex) -> {
            if (ex != null) {
                ErrorHandler.handleError(actor, "ban a member from the Faction", "SQL_FACTION_BAN", ex);
                return;
            }

            if (Settings.FACTION_MANAGE_BROADCAST_BANS.getBoolean()) {
                createBroadcast(actor, Messages.FACTION_MANAGE_BAN_BROADCAST.get("%banned%", player.getName()));
            }
        });
    }

    /**
     * Remove a ban from a player.
     *
     * @param player {@link OfflinePlayer}
     */
    public CompletableFuture<Void> unbanPlayer(OfflinePlayer player) {
        return SkyFactionsReborn.databaseHandler.unbanPlayer(name, player);
    }

    /**
     * @param player Player to check ban status of [{@link Player}]
     * @return {@link Boolean}
     */
    public CompletableFuture<Boolean> isPlayerBanned(OfflinePlayer player) {
        return SkyFactionsReborn.databaseHandler.isPlayerBanned(name, player);
    }

    /**
     * Get a list of all the banned players.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public CompletableFuture<List<OfflinePlayer>> getBannedPlayers() {
        return SkyFactionsReborn.databaseHandler.getBannedPlayers(name);
    }

    /**
     * Remove a player from the faction (willingly).
     *
     * @param player Player who is leaving [{@link Player}]
     */
    public CompletableFuture<Void> leaveFaction(OfflinePlayer player) {
        // todo rem
        return SkyFactionsReborn.databaseHandler.leaveFaction(name, player);
    }

    /**
     * Add a new member to the Faction.
     *
     * @param playerUUID UUID of the player to add [{@link Player}]
     */
    public CompletableFuture<Void> addFactionMember(UUID playerUUID) {
        return SkyFactionsReborn.databaseHandler.addFactionMember(playerUUID, name).thenAccept((ignored) -> {
            members.add(Bukkit.getOfflinePlayer(playerUUID));
            createAuditLog(playerUUID, AuditLogType.PLAYER_JOIN, "player joined the faction temporary message");
        });
    }

    /**
     * Get all audit logs (player join, kick, leave, ban, etc.) of the Faction.
     *
     * @return {@link List<AuditLogData>}
     */
    public CompletableFuture<List<AuditLogData>> getAuditLogs() {
        return SkyFactionsReborn.databaseHandler.getAuditLogs(name);
    }

    /**
     * Create an audit log for the Faction.
     *
     * @param playerUUID   UUID of the player in question [{@link UUID}]
     * @param type         {@link AuditLogType} Type of audit log.
     * @param replacements Values to replace.
     */
    public CompletableFuture<Void> createAuditLog(UUID playerUUID, AuditLogType type, Object... replacements) {
        return SkyFactionsReborn.databaseHandler.createAuditLog(playerUUID, type.getTitle(replacements), type.getDescription(replacements), name);
    }

    /**
     * Get all incoming join requests to the Faction.
     *
     * @return {@link List<InviteData>}
     */
    public CompletableFuture<List<InviteData>> getJoinRequests() {
        return SkyFactionsReborn.databaseHandler.getInvitesOfType(name, "incoming");
    }

    /**
     * Get all outgoing invites from the Faction.
     *
     * @return {@link List<InviteData>}
     */
    public CompletableFuture<List<InviteData>> getOutgoingInvites() {
        return SkyFactionsReborn.databaseHandler.getInvitesOfType(name, "outgoing");
    }

    /**
     * Invite another player to your faction.
     *
     * @param player Player to invite to the faction [{@link Player}]
     */
    public CompletableFuture<Void> createInvite(OfflinePlayer player, Player inviter) {
        return CompletableFuture.allOf(
                SkyFactionsReborn.databaseHandler.createInvite(player.getUniqueId(), name, "outgoing", inviter),
                createAuditLog(player.getUniqueId(), AuditLogType.INVITE_CREATE, "%inviter%", inviter.getName(), "%player_name%", player.getName())
        ).whenComplete((ignored, ex) -> {
            if (player.isOnline()) {
                Messages.FACTION_INVITE_NOTIFICATION.send(player.getPlayer());
            } else {
                NotificationAPI.createNotification(player.getUniqueId(), NotificationType.INVITE_CREATE, "%player_name%", inviter.getName(), "%faction_name%", name);
            }
        });
    }

    /**
     * Create a join request to this Faction.
     *
     * @param player Player who is requesting to join this faction [{@link Player}]
     */
    public CompletableFuture<Void> createJoinRequest(OfflinePlayer player) {
        return CompletableFuture.allOf(
                createAuditLog(player.getUniqueId(), AuditLogType.JOIN_REQUEST_CREATE, "%player_name%", player.getName()),
                SkyFactionsReborn.databaseHandler.createInvite(player.getUniqueId(), name, "incoming", null)
        ).thenAccept((ignored) -> {
            List<OfflinePlayer> users = Stream.concat(getModerators().stream(), getAdmins().stream()).collect(Collectors.toList());
            users.add(getOwner());

            for (OfflinePlayer user : users) {
                if (user.isOnline()) {
                    Messages.JOIN_REQUEST_NOTIFICATION.send(user.getPlayer());
                }
            }
        });
    }

    public boolean isOwner(Player player) {
        return getOwner().equals(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public boolean isModerator(Player player) {
        return getModerators().contains(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    public boolean isAdmin(Player player) {
        return getAdmins().contains(Bukkit.getOfflinePlayer(player.getUniqueId()));
    }

    /**
     * Revoke a Faction invite.
     *
     * @param data  Data of the Invite [{@link InviteData}]
     * @param actor Player who revoked the invite [{@link Player}]
     */
    public CompletableFuture<Void> revokeInvite(InviteData data, Player actor) {
        return CompletableFuture.allOf(
                createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.INVITE_REVOKE, "%player%", actor.getName(), "%invited%", data.getPlayer().getName()),
                SkyFactionsReborn.databaseHandler.revokeInvite(data.getFactionName(), data.getPlayer().getUniqueId(), "outgoing")
        );
    }

    /**
     * Accept a Faction join request.
     *
     * @param data  Data of the Invite [{@link InviteData}]
     * @param actor Player who accepted the invite [{@link Player}]
     */
    public CompletableFuture<Void> acceptJoinRequest(InviteData data, Player actor) {
        return CompletableFuture.allOf(
                createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.JOIN_REQUEST_ACCEPT, "%player%", data.getPlayer().getName(), "%inviter%", actor.getName()),
                SkyFactionsReborn.databaseHandler.acceptJoinRequest(name, data.getPlayer().getUniqueId())
        );
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
     * @param actor Player who rejected the invite [{@link Player}]
     */
    public CompletableFuture<Void> rejectJoinRequest(InviteData data, Player actor) {
        return CompletableFuture.allOf(
                createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.JOIN_REQUEST_REJECT, "%faction_player%", actor.getName(), "%player%", data.getPlayer().getName()),
                SkyFactionsReborn.databaseHandler.revokeInvite(name, data.getPlayer().getUniqueId(), "incoming"),
                NotificationAPI.createNotification(data.getPlayer().getUniqueId(), NotificationType.JOIN_REQUEST_ACCEPT, "%player_name%", actor.getName(), "%faction_name%", name)
        );
    }

    private void cache(OfflinePlayer player, String oldRank, RankType newType) {
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

        }
    }
}