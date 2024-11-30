package net.skullian.skyfactions.paper.notification;

import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotNotificationAPI;
import net.skullian.skyfactions.paper.config.types.Messages;
import net.skullian.skyfactions.paper.api.SpigotPlayerAPI;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NotificationTask {

    public static BukkitRunnable initialise(Player player, boolean isInFaction) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                List<NotificationData> notifications = SpigotNotificationAPI.getNotifications(player);
                if (!notifications.isEmpty()) {
                    Messages.UNREAD_NOTIFICATIONS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "count", notifications.size());
                }

                if (isInFaction) {
                    SpigotFactionAPI.getFaction(player.getUniqueId()).thenAccept((faction) -> {
                        if (faction == null) return;
                        if (SpigotNotificationAPI.factionInviteStore.containsKey(faction.getName())) {
                            int factionJoinRequestCount = SpigotNotificationAPI.factionInviteStore.get(faction.getName());
                            if (factionJoinRequestCount > 0 && (faction.isOwner(player) || faction.isAdmin(player) || faction.isModerator(player))) {
                                Messages.NOTIFICATION_PENDING_JOIN_REQUESTS.send(player, SpigotPlayerAPI.getLocale(player.getUniqueId()), "count", factionJoinRequestCount);
                            }
                        }
                    });
                }
            }
        };
    }


}
