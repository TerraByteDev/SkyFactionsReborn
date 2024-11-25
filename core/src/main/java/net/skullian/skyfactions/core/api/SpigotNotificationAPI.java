package net.skullian.skyfactions.core.api;

import net.skullian.skyfactions.common.api.NotificationAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.core.SkyFactionsReborn;
import net.skullian.skyfactions.core.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.core.notification.NotificationTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpigotNotificationAPI extends NotificationAPI {

    public static Map<String, Integer> factionInviteStore = new ConcurrentHashMap<>();
    public static Map<UUID, BukkitTask> tasks = new HashMap<>();

    public static Map<UUID, List<NotificationData>> notifications = new ConcurrentHashMap<>();

    @Override
    public void createCycle(Player player) {
        FactionAPI.getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }

            SkyApi.getInstance().getDatabaseManager().getNotificationManager().getNotifications(player).whenComplete((fetchedNotifs, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                } else notifications.put(player.getUniqueId(), fetchedNotifs);

                if (faction != null && !factionInviteStore.containsKey(faction.getName())) {
                    DefencePlacementHandler.addPlacedDefences(faction.getName());

                    List<InviteData> requests = faction.getJoinRequests();
                    factionInviteStore.put(faction.getName(), requests.size());

                    BukkitTask task = NotificationTask.initialise(player, true).runTaskTimerAsynchronously(SkyFactionsReborn.getInstance(), 0L, (Settings.NOTIFICATIONS_INTERVAL.getInt() * 20L));
                    tasks.put(player.getUniqueId(), task);
                }
            });
        });
    }

    @Override
    public void addNotification(UUID playerUUID, NotificationData notification) {
        if (notifications.containsKey(playerUUID)) notifications.get(playerUUID).add(notification);
    }

    @Override
    public void removeNotification(UUID playerUUID, NotificationData notification) {
        if (notifications.containsKey(playerUUID)) notifications.get(playerUUID).remove(notification);
    }

    @Nullable
    @Override
    public List<NotificationData> getNotifications(UUID playerUUID) {
        return notifications.get(playerUUID);
    }
}
