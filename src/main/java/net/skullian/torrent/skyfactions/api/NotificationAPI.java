package net.skullian.torrent.skyfactions.api;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.types.Settings;
import net.skullian.torrent.skyfactions.notification.NotificationData;
import net.skullian.torrent.skyfactions.notification.NotificationTask;
import net.skullian.torrent.skyfactions.notification.NotificationType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NotificationAPI {

    public static Map<String, Integer> factionInviteStore = new HashMap<>();
    public static Map<UUID, BukkitTask> tasks = new HashMap<>();

    public static void createCycle(Player player) {
        FactionAPI.getFaction(player).whenCompleteAsync((faction, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            if (faction != null && !factionInviteStore.containsKey(faction.getName())) {
                faction.getJoinRequests().whenCompleteAsync((requests, exc) -> {
                    if (exc != null) {
                        exc.printStackTrace();
                        return;
                    }

                    factionInviteStore.put(faction.getName(), requests.size());

                    BukkitTask task = NotificationTask.initialise(player, true).runTaskTimerAsynchronously(SkyFactionsReborn.getInstance(), 0L, (Settings.NOTIFICATIONS_INTERVAL.getInt() * 20L));
                    tasks.put(player.getUniqueId(), task);
                });
            }
        });
    }

    /**
     * Create a notification for a Player.
     *
     * @param playerUUID   UUID of the player who the notification should be sent to [{@link UUID}]
     * @param type         Type of notification [{@link net.skullian.torrent.skyfactions.faction.AuditLogType}]
     * @param replacements Replacements for the notification title / desc.
     */
    public static CompletableFuture<Void> createNotification(UUID playerUUID, NotificationType type, Object... replacements) {
        return SkyFactionsReborn.db.createNotification(playerUUID, type.getTitle(replacements), type.getDescription(replacements));
    }

    /**
     * Get all notifications (unread) of a Player.
     *
     * @param player Player who the notifications should be fetched from [{@link Player}]
     * @return {@link List<NotificationData>}
     */
    public static List<NotificationData> getNotifications(OfflinePlayer player) {
        return SkyFactionsReborn.db.getNotifications(player).join();
    }
}
