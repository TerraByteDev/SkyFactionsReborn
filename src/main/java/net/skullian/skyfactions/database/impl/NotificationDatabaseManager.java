package net.skullian.skyfactions.database.impl;

import net.skullian.skyfactions.database.tables.records.NotificationsRecord;
import net.skullian.skyfactions.notification.NotificationData;
import net.skullian.skyfactions.util.text.TextUtility;
import org.bukkit.OfflinePlayer;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.database.tables.Notifications.NOTIFICATIONS;

public class NotificationDatabaseManager {

    private final DSLContext ctx;

    public NotificationDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<Void> createNotification(UUID playerUUID, String type, String replacements) {
        return CompletableFuture.runAsync(() -> {
            ctx.insertInto(NOTIFICATIONS)
                    .columns(NOTIFICATIONS.UUID, NOTIFICATIONS.TYPE, NOTIFICATIONS.REPLACEMENTS, NOTIFICATIONS.TIMESTAMP)
                    .values(playerUUID.toString(), type, replacements, System.currentTimeMillis())
                    .execute();
        });
    }

    public CompletableFuture<Void> removeNotification(OfflinePlayer player, NotificationData data) {
        return CompletableFuture.runAsync(() -> {
            ctx.deleteFrom(NOTIFICATIONS)
                    .where(NOTIFICATIONS.UUID.eq(player.getUniqueId().toString()), NOTIFICATIONS.TIMESTAMP.eq(data.getTimestamp()))
                    .execute();
        });
    }

    public CompletableFuture<List<NotificationData>> getNotifications(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            Result<NotificationsRecord> results = ctx.selectFrom(NOTIFICATIONS)
                    .where(NOTIFICATIONS.UUID.eq(player.getUniqueId().toString()))
                    .orderBy(NOTIFICATIONS.TIMESTAMP.desc())
                    .fetch();

            List<NotificationData> data = new ArrayList<>();
            for (NotificationsRecord notification : results) {
                data.add(new NotificationData(
                        player.getUniqueId(),
                        notification.getType(),
                        TextUtility.toParts(notification.getReplacements()),
                        notification.getTimestamp()
                ));
            }

            return data;
        });
    }
}
