package net.skullian.torrent.skyfactions.notification;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.api.NotificationAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NotificationTask {

    public static BukkitRunnable initialise(Player player, boolean isInFaction) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                NotificationAPI.getNotifications(Bukkit.getOfflinePlayer(player.getUniqueId())).whenCompleteAsync((data, ex) -> {
                    if (ex != null) {
                        SLogger.fatal("Failed to fetch notifications of player {} - {}", player.getName(), ex.getMessage());
                        ex.printStackTrace();
                        return;
                    }

                    if (!data.isEmpty()) {
                        Messages.UNREAD_NOTIFICATIONS.send(player, "%count%", data.size());
                    }
                });

                if (isInFaction) {
                    FactionAPI.getFaction(player).thenAccept((faction) -> {
                        if (faction == null) return;
                        if (NotificationAPI.factionInviteStore.containsKey(faction.getName())) {
                            int factionJoinRequestCount = NotificationAPI.factionInviteStore.get(faction.getName());
                            if (factionJoinRequestCount > 0 && (faction.isOwner(player) || faction.isAdmin(player) || faction.isModerator(player))) {
                                Messages.NOTIFICATION_PENDING_JOIN_REQUESTS.send(player, "%count%", factionJoinRequestCount);
                            }
                        }
                    });
                }
            }
        };
    }


}
