package net.skullian.skyfactions.common.database.impl;

import net.skullian.skyfactions.common.database.tables.records.NotificationsRecord;
import net.skullian.skyfactions.common.notification.NotificationData;
import net.skullian.skyfactions.common.user.SkyUser;
import net.skullian.skyfactions.common.util.text.TextUtility;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.skullian.skyfactions.common.database.tables.Notifications.NOTIFICATIONS;

public class NotificationDatabaseManager {

    private final DSLContext ctx;

    public NotificationDatabaseManager(DSLContext ctx) {
        this.ctx = ctx;
    }

    public CompletableFuture<Void> createNotifications(List<NotificationData> notifications) {
        return CompletableFuture.runAsync(() -> {
            if (notifications.isEmpty()) return;
            ctx.transaction((Configuration trx) -> {
                for (NotificationData notification : notifications) {
                    trx.dsl().insertInto(NOTIFICATIONS)
                            .columns(NOTIFICATIONS.UUID, NOTIFICATIONS.TYPE, NOTIFICATIONS.REPLACEMENTS, NOTIFICATIONS.TIMESTAMP)
                            .values(notification.getUuid().toString(), notification.getType(), Arrays.toString(notification.getReplacements()), System.currentTimeMillis())
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<Void> removeNotifications(List<NotificationData> notifications) {
        return CompletableFuture.runAsync(() -> {
            if (notifications.isEmpty()) return;
            ctx.transaction((Configuration trx) -> {
                for (NotificationData notification : notifications) {
                    trx.dsl().deleteFrom(NOTIFICATIONS)
                            .where(NOTIFICATIONS.UUID.eq(notification.getUuid().toString()), NOTIFICATIONS.TIMESTAMP.eq(notification.getTimestamp()))
                            .execute();
                }
            });
        });
    }

    public CompletableFuture<List<NotificationData>> getNotifications(SkyUser player) {
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
                        TextUtility.convertFromString(notification.getReplacements()),
                        notification.getTimestamp()
                ));
            }

            return data;
        });
    }
}
