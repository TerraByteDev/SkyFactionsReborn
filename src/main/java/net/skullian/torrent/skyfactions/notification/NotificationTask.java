package net.skullian.torrent.skyfactions.notification;

import net.skullian.torrent.skyfactions.api.FactionAPI;
import net.skullian.torrent.skyfactions.api.NotificationAPI;
import net.skullian.torrent.skyfactions.config.types.Messages;
import net.skullian.torrent.skyfactions.faction.Faction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NotificationTask {

    public static BukkitRunnable initialise(Player player, boolean isInFaction) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                List<NotificationData> data = NotificationAPI.getNotifications(Bukkit.getOfflinePlayer(player.getUniqueId()));
                if (!data.isEmpty()) {
                    Messages.UNREAD_NOTIFICATIONS.send(player, "%count%", data.size());
                }

                if (isInFaction) {
                    Faction faction = FactionAPI.getFaction(player);
                    if (faction == null) return;
                    if (NotificationAPI.factionInviteStore.containsKey(faction.getName())) {
                        int factionJoinRequestCount = NotificationAPI.factionInviteStore.get(faction.getName());
                        if (factionJoinRequestCount > 0 && (faction.isOwner(player) || faction.isAdmin(player) || faction.isModerator(player))) {
                            Messages.NOTIFICATION_PENDING_JOIN_REQUESTS.send(player, "%count%", factionJoinRequestCount);
                        }
                    }
                }
            }
        };
    }
}
