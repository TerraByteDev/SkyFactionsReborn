package net.skullian.skyfactions.paper.api;

import net.skullian.skyfactions.common.api.NotificationAPI;
import net.skullian.skyfactions.common.api.SkyApi;
import net.skullian.skyfactions.common.config.types.Settings;
import net.skullian.skyfactions.common.database.struct.InviteData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.SLogger;
import net.skullian.skyfactions.paper.SkyFactionsReborn;
import net.skullian.skyfactions.paper.event.defence.DefencePlacementHandler;
import net.skullian.skyfactions.paper.notification.NotificationTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SpigotNotificationAPI extends NotificationAPI {

    public static Map<UUID, BukkitTask> tasks = new HashMap<>();

    @Override
    public void createCycle(SkyUser player) {
        SkyApi.getInstance().getFactionAPI().getFaction(player.getUniqueId()).whenComplete((faction, ex) -> {
            if (ex != null) {
                SLogger.fatal("Failed to fetch faction for player {} - {}", player.getUniqueId(), ex);
                return;
            }

            SkyApi.getInstance().getDatabaseManager().getNotificationManager().getNotifications(player).whenComplete((fetchedNotifs, throwable) -> {
                if (throwable != null) {
                    SLogger.fatal("Failed to fetch notifications for player {} - {}", player.getUniqueId(), throwable);
                    notifications.put(player.getUniqueId(), new ArrayList<>());
                    return;
                } else notifications.put(player.getUniqueId(), fetchedNotifs);

                if (faction != null && !getFactionInviteStore().containsKey(faction.getName())) {
                    DefencePlacementHandler.addPlacedDefences(faction.getName());

                    List<InviteData> requests = faction.getJoinRequests();
                    getFactionInviteStore().put(faction.getName(), requests.size());

                    BukkitTask task = NotificationTask.initialise(player, true).runTaskTimerAsynchronously(SkyFactionsReborn.getInstance(), 0L, (Settings.NOTIFICATIONS_INTERVAL.getInt() * 20L));
                    tasks.put(player.getUniqueId(), task);
                }
            });
        });
    }
}
