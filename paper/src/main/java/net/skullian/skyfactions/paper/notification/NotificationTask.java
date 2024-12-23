package net.skullian.skyfactions.paper.notification;

import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Messages;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.paper.api.SpigotFactionAPI;
import net.skullian.skyfactions.paper.api.SpigotNotificationAPI;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NotificationTask {

    public static BukkitRunnable initialise(SkyUser player, boolean isInFaction) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                String locale = SkyApi.getInstance().getPlayerAPI().getLocale(player.getUniqueId());
                List<NotificationData> notifications = SkyApi.getInstance().getNotificationAPI().getNotifications(player.getUniqueId());
                if (notifications != null && !notifications.isEmpty()) {
                    Messages.UNREAD_NOTIFICATIONS.send(player, locale, "count", notifications.size());
                }

                if (isInFaction) {
                    SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).thenAccept((faction) -> {
                        if (faction == null) return;
                        if (SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().containsKey(faction.getName())) {
                            int factionJoinRequestCount = SkyApi.getInstance().getNotificationAPI().getFactionInviteStore().get(faction.getName());
                            if (factionJoinRequestCount > 0 && (faction.isOwner(player) || faction.isAdmin(player) || faction.isModerator(player))) {
                                Messages.NOTIFICATION_PENDING_JOIN_REQUESTS.send(player, locale, "count", factionJoinRequestCount);
                            }
                        }
                    });
                }
            }
        };
    }


}
