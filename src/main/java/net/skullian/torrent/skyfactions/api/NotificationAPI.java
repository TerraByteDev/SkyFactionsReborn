package net.skullian.torrent.skyfactions.api;

import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import net.skullian.torrent.skyfactions.config.Settings;
import net.skullian.torrent.skyfactions.faction.Faction;
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
        Faction faction = FactionAPI.getFaction(player);
        if (faction != null && !factionInviteStore.containsKey(faction.getName())) {
            factionInviteStore.put(faction.getName(), faction.getJoinRequests().size());
        }

        BukkitTask task = NotificationTask.initialise(player, FactionAPI.isInFaction(player)).runTaskTimerAsynchronously(SkyFactionsReborn.getInstance(), 0L, (Settings.NOTIFICATIONS_INTERVAL.getInt() * 20L));
        tasks.put(player.getUniqueId(), task);
    }

    /**
     * Create a notification for a Player.
     *
     * @param player Player who the notification should be sent to [{@link Player}]
     * @param type Type of notification [{@link net.skullian.torrent.skyfactions.faction.AuditLogType}]
     * @param replacements Replacements for the notification title / desc.
     */
    public static void createNotification(OfflinePlayer player, NotificationType type, Object... replacements) {
        SkyFactionsReborn.db.createNotification(player, type.getTitle(replacements), type.getDescription(replacements)).join();
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
