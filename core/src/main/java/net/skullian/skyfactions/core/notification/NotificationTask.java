package net.skullian.skyfactions.core.notification;

import net.skullian.skyfactions.core.api.FactionAPI;
import net.skullian.skyfactions.core.api.SpigotNotificationAPI;
import net.skullian.skyfactions.core.config.types.Messages;
import net.skullian.skyfactions.core.api.SpigotPlayerAPI;
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
                    FactionAPI.getFaction(player.getUniqueId()).thenAccept((faction) -> {
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
