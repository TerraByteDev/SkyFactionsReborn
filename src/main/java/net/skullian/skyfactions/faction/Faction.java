package net.skullian.skyfactions.faction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.skullian.skyfactions.api.FactionAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.skullian.skyfactions.SkyFactionsReborn;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.config.types.Settings;
import net.skullian.skyfactions.database.struct.AuditLogData;
import net.skullian.skyfactions.database.struct.InviteData;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.island.FactionIsland;
import net.skullian.skyfactions.notification.NotificationType;
import net.skullian.skyfactions.util.text.TextUtility;

@AllArgsConstructor
@Getter
public class Faction {

    private FactionIsland island;
    private String name;
    private long last_raid;
    private int level;
    private OfflinePlayer owner;
    private List<OfflinePlayer> admins;
    private List<OfflinePlayer> moderators;
    private List<OfflinePlayer> fighters;
    private List<OfflinePlayer> members;
    private String motd;
    public int runes;
    public int gems;
    public String locale;
    public List<OfflinePlayer> bannedPlayers;

    public int getRunes() {
        if (SkyFactionsReborn.cacheService.factionsToCache.containsKey(this)) return (runes += SkyFactionsReborn.cacheService.factionsToCache.get(this).getRunes());
            else return runes;
    }

    public int getGems() {
        if (SkyFactionsReborn.cacheService.factionsToCache.containsKey(this)) return (gems += SkyFactionsReborn.cacheService.factionsToCache.get(this).getGems());
            else return gems;
    }

    /**
     * Update the name of the faction.
     *
     * @param newName New name of the faction.
     **/
    public CompletableFuture<Void> updateName(String newName) {
        name = newName;
        return SkyFactionsReborn.databaseManager.factionsManager.updateFactionName(newName, name);
    }

    /**
     * Change the rank of a player.
     *
     * @param player  Player in question.
     * @param newRank {@link RankType} New Rank of the player.
     */
    public void modifyPlayerRank(OfflinePlayer player, RankType newRank, Player actor) {
        RankType oldRank = getRankType(player.getUniqueId());
        SkyFactionsReborn.cacheService.updatePlayerRank(this, player.getUniqueId(), newRank);
        cache(player, oldRank.getRankValue(), newRank);
        NotificationAPI.createNotification(player.getUniqueId(), NotificationType.RANK_UPDATED, "new_rank", getRank(player.getUniqueId()), "player_name", actor.getName());
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
        return CompletableFuture.allOf(createAuditLog(actor.getUniqueId(), AuditLogType.MOTD_UPDATE, "player_name", actor.getName(), "new_motd", MOTD), SkyFactionsReborn.databaseManager.factionsManager.updateFactionMOTD(name, MOTD));
    }

    /**
     * Broadcast a message to the Faction's online members.
     *
     * @param message Message to broadcast [{@link Messages}]
     */
    public void createBroadcast(OfflinePlayer broadcaster, Messages message, Object... replacements) {
        List<OfflinePlayer> players = getAllMembers();
        for (OfflinePlayer player : players) {
            if (player.isOnline()) {
                String locale = PlayerHandler.getLocale(player.getUniqueId());

                Component model = TextUtility.fromList(Messages.FACTION_BROADCAST_MODEL.getStringList(locale), locale, player, "broadcaster", broadcaster.getName(), "broadcast",
                    Messages.replace(message.getString(locale), locale, player.getPlayer(), replacements)
                );
        
                player.getPlayer().sendMessage(model);
            }
        }
    }

    /**
     * Broadcast a message to the Faction's online members.
     *
     * @param message Message to broadcast [{@link String}]
     */
    public void createBroadcast(OfflinePlayer broadcaster, String message) {
        List<OfflinePlayer> players = getAllMembers();
        for (OfflinePlayer player : players) {
            if (player.isOnline()) {
                String locale = PlayerHandler.getLocale(player.getUniqueId());

                Component model = TextUtility.fromList(Messages.FACTION_BROADCAST_MODEL.getStringList(locale), locale, player, "broadcaster", broadcaster.getName(), "broadcast",
                    Messages.replace(message, locale, player.getPlayer()));
                
                player.getPlayer().sendMessage(model);
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
        SkyFactionsReborn.cacheService.addRunes(this, addition);
    }

    /**
     * Remove runes from a Faction.
     *
     * @param subtraction Amount of runes to remove [{@link Integer}]
     */
    public void subtractRunes(int subtraction) {
        runes -= subtraction;
        SkyFactionsReborn.cacheService.subtractRunes(this, subtraction);
    }

    /**
     * Add gems to the Faction's gem balance.
     *
     * @param addition Gems to add [{@link Integer}]
     */
    public void addGems(int addition) {
        gems += addition;
        SkyFactionsReborn.cacheService.addGems(this, addition);
    }

    /**
     * Removes gems to the Factions' gem balance.
     *
     * @param subtraction Gems to remove [{@link Integer}]
     */
    public void subtractGems(int subtraction) {
        gems -= subtraction;
        SkyFactionsReborn.cacheService.subtractGems(this, subtraction);
    }

    public void updateLocale(String newLocale) {
        SkyFactionsReborn.cacheService.updateLocale(this, newLocale);
    }

    /**
     * Kick a player from the Faction for a specific reason.
     *
     * @param player Player to kick [{@link Player}]
     */
    public void kickPlayer(OfflinePlayer player, Player actor) {
        SkyFactionsReborn.cacheService.removeFactionMember(this, player);
        if (Settings.FACTION_MANAGE_BROADCAST_KICKS.getBoolean()) {
            createBroadcast(actor, Messages.FACTION_MANAGE_KICK_BROADCAST,"<kicked>", player.getName());
        }
    }

    /**
     * Ban a player from the faction.
     *
     * @param player Player to ban [{@link Player}]
     */
    public void banPlayer(OfflinePlayer player, Player actor) {
        SkyFactionsReborn.cacheService.banFactionMember(this, player);
        bannedPlayers.add(player);
        createAuditLog(player.getUniqueId(), AuditLogType.PLAYER_BAN, "banned", player.getName(), "player", actor.getName());
        if (Settings.FACTION_MANAGE_BROADCAST_BANS.getBoolean()) {
            createBroadcast(actor, Messages.FACTION_MANAGE_BAN_BROADCAST,"<banned>", player.getName());
        }
    }

    /**
     * Remove a ban from a player.
     *
     * @param player {@link OfflinePlayer}
     */
    public void unbanPlayer(OfflinePlayer player) {
        bannedPlayers.remove(player);
        SkyFactionsReborn.cacheService.unbanFactionMember(this, player); // todo audit log & ban viewing
    }

    /**
     * @param player Player to check ban status of [{@link Player}]
     * @return {@link Boolean}
     */
    public boolean isPlayerBanned(OfflinePlayer player) {
        return bannedPlayers.contains(player);
    }

    /**
     * Get a list of all the banned players.
     *
     * @return {@link List<OfflinePlayer>}
     */
    public CompletableFuture<List<OfflinePlayer>> getBannedPlayers() {
        return SkyFactionsReborn.databaseManager.factionsManager.getBannedPlayers(name);
    }

    /**
     * Remove a player from the faction (willingly).
     *
     * @param player Player who is leaving [{@link Player}]
     */
    public void leaveFaction(OfflinePlayer player) {
        removeFromFaction(player);
        SkyFactionsReborn.cacheService.removeFactionMember(this, player);
    }

    /**
     * Add a new member to the Faction.
     *
     * @param player UUID of the player to add [{@link OfflinePlayer}]
     */
    public void addFactionMember(OfflinePlayer player) {
        SkyFactionsReborn.cacheService.addFactionMember(this, player);
        members.add(player);
        createAuditLog(player.getUniqueId(), AuditLogType.PLAYER_JOIN, "player_name", player.getName());
        createBroadcast(player, Messages.FACTION_JOIN_BROADCAST, "<player_name>", player.getName());
    }

    /**
     * Get all audit logs (player join, kick, leave, ban, etc.) of the Faction.
     *
     * @return {@link List<AuditLogData>}
     */
    public CompletableFuture<List<AuditLogData>> getAuditLogs() {
        return SkyFactionsReborn.databaseManager.factionAuditLogManager.getAuditLogs(name);
    }

    /**
     * Create an audit log for the Faction.
     *
     * @param playerUUID   UUID of the player in question [{@link UUID}]
     * @param type         {@link AuditLogType} Type of audit log.
     * @param replacements Values to replace.
     */
    public CompletableFuture<Void> createAuditLog(UUID playerUUID, AuditLogType type, Object... replacements) {
        return SkyFactionsReborn.databaseManager.factionAuditLogManager.createAuditLog(playerUUID, type.name(), Arrays.toString(replacements), name);
    }

    /**
     * Get all incoming join requests to the Faction.
     *
     * @return {@link List<InviteData>}
     */
    public CompletableFuture<List<InviteData>> getJoinRequests() {
        return SkyFactionsReborn.databaseManager.factionInvitesManager.getInvitesOfType(name, "incoming");
    }

    /**
     * Get all outgoing invites from the Faction.
     *
     * @return {@link List<InviteData>}
     */
    public CompletableFuture<List<InviteData>> getOutgoingInvites() {
        return SkyFactionsReborn.databaseManager.factionInvitesManager.getInvitesOfType(name, "outgoing");
    }

    /**
     * Invite another player to your faction.
     *
     * @param player Player to invite to the faction [{@link Player}]
     */
    public CompletableFuture<Void> createInvite(OfflinePlayer player, Player inviter) {
        return CompletableFuture.allOf(
                SkyFactionsReborn.databaseManager.factionInvitesManager.createFactionInvite(player.getUniqueId(), name, "outgoing", inviter),
                createAuditLog(player.getUniqueId(), AuditLogType.INVITE_CREATE, "inviter", inviter.getName(), "player_name", player.getName())
        ).whenComplete((ignored, ex) -> {
            if (player.isOnline()) {
                Messages.FACTION_INVITE_NOTIFICATION.send(player.getPlayer(), player.getPlayer().locale().getLanguage());
            } else {
                NotificationAPI.createNotification(player.getUniqueId(), NotificationType.INVITE_CREATE, "player_name", inviter.getName(), "faction_name", name);
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
                createAuditLog(player.getUniqueId(), AuditLogType.JOIN_REQUEST_CREATE, "player_name", player.getName()),
                SkyFactionsReborn.databaseManager.factionInvitesManager.createFactionInvite(player.getUniqueId(), name, "incoming", null)
        ).thenAccept((ignored) -> {
            List<OfflinePlayer> users = Stream.concat(getModerators().stream(), getAdmins().stream()).collect(Collectors.toList());
            users.add(getOwner());

            for (OfflinePlayer user : users) {
                if (user.isOnline()) {
                    Messages.JOIN_REQUEST_NOTIFICATION.send(user.getPlayer(), user.getPlayer().locale().getLanguage());
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
                createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.INVITE_REVOKE, "player", actor.getName(), "invited", data.getPlayer().getName()),
                SkyFactionsReborn.databaseManager.factionInvitesManager.revokeInvite(data.getPlayer().getUniqueId(), data.getFactionName(), "outgoing")
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
                createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.JOIN_REQUEST_ACCEPT, "player", data.getPlayer().getName(), "inviter", actor.getName()),
                SkyFactionsReborn.databaseManager.factionInvitesManager.acceptJoinRequest(data.getPlayer().getUniqueId(), name)
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
                createAuditLog(data.getPlayer().getUniqueId(), AuditLogType.JOIN_REQUEST_REJECT, "faction_player", actor.getName(), "player", data.getPlayer().getName()),
                SkyFactionsReborn.databaseManager.factionInvitesManager.revokeInvite(data.getPlayer().getUniqueId(), name, "incoming"),
                NotificationAPI.createNotification(data.getPlayer().getUniqueId(), NotificationType.JOIN_REQUEST_ACCEPT, "player_name", actor.getName(), "faction_name", name)
        );
    }

    /**
     * Get the configured rank title of a member.
     * @param playerUUID UUID of the player {@link UUID}
     *
     * @return The rank of the player. {@link String}
     */
    public String getRank(UUID playerUUID) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        String locale = player.isOnline() ? PlayerHandler.getLocale(player.getUniqueId()) : Messages.getDefaulLocale();

        if (owner.equals(player)) return Messages.FACTION_OWNER_TITLE.getString(locale);
        if (admins.contains(player)) return Messages.FACTION_ADMIN_TITLE.getString(locale);
        if (moderators.contains(player)) return Messages.FACTION_MODERATOR_TITLE.getString(locale);
        if (fighters.contains(player)) return Messages.FACTION_FIGHTER_TITLE.getString(locale);
        if (members.contains(player)) return Messages.FACTION_MEMBER_TITLE.getString(locale);

        return "<red>N/A";
    }

    public RankType getRankType(UUID playerUUID) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);

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
            case "members":
                members.add(player);
                break;
        }
    }

    private void removeFromFaction(OfflinePlayer player) {
        FactionAPI.factionCache.remove(player.getUniqueId());
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
