package net.skullian.skyfactions.notification;

import net.skullian.skyfactions.api.FactionAPI;
import net.skullian.skyfactions.api.NotificationAPI;
import net.skullian.skyfactions.config.types.Messages;
import net.skullian.skyfactions.event.PlayerHandler;
import net.skullian.skyfactions.util.SLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NotificationTask {

    public static BukkitRunnable initialise(Player player, boolean isInFaction) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                List<NotificationData> notifications = NotificationAPI.getNotifications(player);
                if (!notifications.isEmpty()) {
                    Messages.UNREAD_NOTIFICATIONS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "count", notifications.size());
                }

                if (isInFaction) {
                    FactionAPI.getFaction(player.getUniqueId()).thenAccept((faction) -> {
                        if (faction == null) return;
                        if (NotificationAPI.factionInviteStore.containsKey(faction.getName())) {
                            int factionJoinRequestCount = NotificationAPI.factionInviteStore.get(faction.getName());
                            if (factionJoinRequestCount > 0 && (faction.isOwner(player) || faction.isAdmin(player) || faction.isModerator(player))) {
                                Messages.NOTIFICATION_PENDING_JOIN_REQUESTS.send(player, PlayerHandler.getLocale(player.getUniqueId()), "count", factionJoinRequestCount);
                            }
                        }
                    });
                }
            }
        };
    }


}
