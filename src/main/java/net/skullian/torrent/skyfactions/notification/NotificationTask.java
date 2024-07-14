package net.skullian.torrent.skyfactions.notification;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.config.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NotificationTask {

    public static BukkitRunnable initialise(Player player, boolean isInFaction) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (NotificationHandler.playerInviteStore.containsKey(player.getUniqueId())) {
                    int playerInviteCount = NotificationHandler.playerInviteStore.get(player.getUniqueId());
                    if (playerInviteCount > 0) {
                        Messages.NOTIFICATION_PENDING_FACTION_INVITATIONS.send(player, "%count%", playerInviteCount);
                    }
                }

                if (isInFaction) {
                    Faction faction = FactionAPI.getFaction(player);
                    if (NotificationHandler.factionInviteStore.containsKey(faction.getName())) {
                        int factionJoinRequestCount = NotificationHandler.factionInviteStore.get(faction.getName());
                        if (factionJoinRequestCount > 0 && (faction.isOwner(player) || faction.isAdmin(player) || faction.isModerator(PLAYER))) {
                            Messages.NOTIFICATION_PENDING_JOIN_REQUESTS.send(player, "%count%", factionJoinRequestCount);
                        }
                    }
                }
            }
        };

        return runnable;
    }
}
